<?php

/**
 * 出入境证件核验接口示例代码
 * 接口文档: https://support.dun.163.com/documents/391676076156063744?docId=391677747099668480
 */

/** 产品密钥ID，产品标识 */
define("SECRETID", "your_secret_id");
/** 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露 */
define("SECRETKEY", "your_secret_key");
/** 业务ID，易盾根据产品业务特点分配 */
define("BUSINESSID", "your_business_id");
/** 易盾出入境证件核验接口地址 */
define("API_URL", "https://verify.dun.163.com/v1/foreign/check");
/** api version */
define("VERSION", "v1");
/** API timeout*/
define("API_TIMEOUT", 10);
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
        "cardNo" => "341622123456784317",
        "verifyType" => "1",
        "nation" => "CHN"
    );
    $ret = check($params);
    printf("reponse:</br>" . json_encode($ret));
    if ($ret["code"] == 200) {
        printf(
            "</br>taskId=%s,出入境证件核验结果:%s,原因详情:%s,具体请参考接口文档说明",
            $ret["result"]["taskId"],
            $ret["result"]["status"],
            $ret["result"]["reasonType"]
        );
    } else {
        printf("</br>请求错误,请检查参数或重试");
    }
}
main();
