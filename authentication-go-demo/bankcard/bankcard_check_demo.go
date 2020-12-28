/**
 * 银行卡三/四要素认证接口示例代码
 * 接口文档: https://support.dun.163.com/documents/391676076156063744?docId=391677771764887552
 */
package main

import (
	"crypto/md5"
	"encoding/hex"
	"fmt"
	"io/ioutil"
	"math/rand"
	"net/http"
	"net/url"
	"sort"
	"strconv"
	"strings"
	"time"

	"github.com/bitly/go-simplejson"
	"github.com/tjfoc/gmsm/sm3"
)

const (
	apiURL     = "https://verify.dun.163.com/v1/bankcard/check"
	version    = "v1"
	secretID   = "your_secretId"   //产品密钥ID，产品标识
	secretKey  = "your_secretKey"  //产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露
	businessID = "your_businessId" //业务ID，易盾根据产品业务特点分配
)

//请求易盾接口
func check(params url.Values) *simplejson.Json {
	params["secretId"] = []string{secretID}
	params["businessId"] = []string{businessID}
	params["version"] = []string{version}
	params["timestamp"] = []string{strconv.FormatInt(time.Now().UnixNano()/1000000, 10)}
	params["nonce"] = []string{strconv.FormatInt(rand.New(rand.NewSource(time.Now().UnixNano())).Int63n(10000000000), 10)}
	params["signature"] = []string{genSignature(params)}

	resp, err := http.Post(apiURL, "application/x-www-form-urlencoded", strings.NewReader(params.Encode()))

	if err != nil {
		fmt.Println("调用API接口失败:", err)
		return nil
	}

	defer resp.Body.Close()

	contents, _ := ioutil.ReadAll(resp.Body)
	result, _ := simplejson.NewJson(contents)
	return result
}

//生成签名信息
func genSignature(params url.Values) string {
	var paramStr string
	keys := make([]string, 0, len(params))
	for k := range params {
		keys = append(keys, k)
	}
	sort.Strings(keys)
	for _, key := range keys {
		paramStr += key + params[key][0]
	}
	paramStr += secretKey
	if params["signatureMethod"] != nil && params["signatureMethod"][0] == "SM3" {
		sm3Reader := sm3.New()
		sm3Reader.Write([]byte(paramStr))
		return hex.EncodeToString(sm3Reader.Sum(nil))
	}
	md5Reader := md5.New()
	md5Reader.Write([]byte(paramStr))
	return hex.EncodeToString(md5Reader.Sum(nil))
}

func main() {
	params := url.Values{
		"name":       []string{"张三"},
		"idCardNo":   []string{"341622123456141234"},
		"bankCardNo": []string{"6222123412343569"},
		"phone":      []string{"13358123456"},
	}

	ret := check(params)

	code, _ := ret.Get("code").Int()
	message, _ := ret.Get("msg").String()
	if code == 200 {
		hitsjson, _ := ret.Get("result").MarshalJSON()
		fmt.Printf("%s\n", hitsjson)
		taskId, _ := ret.Get("result").Get("taskId").String()
		status, _ := ret.Get("result").Get("status").String()
		reasonType, _ := ret.Get("result").Get("reasonType").String()
		fmt.Printf("taskId =%s, 银行卡认证结果:%s, 原因详情:%s, 具体请参考接口文档说明\n", taskId, status, reasonType)
	} else {
		fmt.Printf("ERROR: code=%d, msg=%s\n", code, message)
	}
}
