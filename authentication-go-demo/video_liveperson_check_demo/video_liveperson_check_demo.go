/**
 * 单视频活体检测接口示例代码
 * 接口文档: https://support.dun.163.com/documents/391676076156063744?docId=411231744954781696
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
	apiURL     = "https://verify.dun.163.com/v1/liveperson/h5/check"
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
	params["actionVideos"] = []string{"['BASE编码后的视频文件']"}

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
		"videoType":  []string{"2"},
		"actions":    []string{"[4]"},
		"needAvatar": []string{"false"},
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
		avatar, _ := ret.Get("result").Get("avatar").String()
		fmt.Printf("taskId=%s,认证结果:%s,原因详情:%s,图片类型:%s,抓取头像照片:%s, 具体请参考接口文档说明\n", taskId, status, reasonType, avatar)
	} else {
		fmt.Printf("ERROR: code=%d, msg=%s\n", code, message)
	}
}
