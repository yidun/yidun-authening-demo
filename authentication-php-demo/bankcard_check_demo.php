<?php

/**
 * 银行卡三/四要素认证接口示例代码
 * 接口文档: https://support.dun.163.com/documents/391676076156063744?docId=391677771764887552
 */

/** 产品密钥ID，产品标识 */
define("SECRETID", "your_secret_id");
/** 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露 */
define("SECRETKEY", "your_secret_key");
/** 业务ID，易盾根据产品业务特点分配 */
define("BUSINESSID", "your_business_id");
/** 易盾实人身份认证接口地址 */
define("API_URL", "https://verify.dun.163.com/v1/bankcard/check");
/** api version */
define("VERSION", "v1");
/** API timeout*/
define("API_TIMEOUT", 10);
/** php内部使用的字符串编码 */
define("INTERNAL_STRING_CHARSET", "auto");
require("util.php");

/**
 * 请求接口简单封装
 * $params 请求参数
 */
function check($params)
{
    $params["secretId"] = SECRETID;
    $params["businessId"] = BUSINESSID;
    $params["version"] = VERSION;
    $params["timestamp"] = time() * 1000; // time in milliseconds
    $params["nonce"] = sprintf("%d", rand()); // random int

    $params = toUtf8($params);
    $params["signature"] = gen_signature(SECRETKEY, $params);

    $result = curl_post($params, API_URL, API_TIMEOUT);
    return json_decode($result, true);
}

// 简单测试
function main()
{
    $params = array(
        "name" => "张三",
        "idCardNo" => "123456908776554311",
        "bankCardNo" => "6222123412343569",
        "phone" => "13145678911"
    );
    $ret = check($params);
    printf("reponse:</br>" . json_encode($ret));
    if ($ret["code"] == 200) {
        $status = $ret["result"]["status"];
        $taskId = $ret["result"]["taskId"];
        $reasonType = $ret["result"]["reasonType"];
        printf("</br>taskId=%s,银行卡认证结果:%s,原因详情:%s,具体请参考接口文档说明", $taskId, $status, $reasonType);
    } else {
        printf("</br>请求错误,请检查参数或重试");
    }
}
main();
