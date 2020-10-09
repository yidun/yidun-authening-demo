<?php
/** 音频提交检测接口 */
/** 产品密钥ID，产品标识 */
define("SECRETID", "your_secret_id");
/** 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露 */
define("SECRETKEY", "your_secret_key");
/** 业务ID，易盾根据产品业务特点分配 */
define("BUSINESSID", "your_business_id");
/** 易盾反垃圾云服务音频检测接口地址 */
define("API_URL", "https://www.baidu.com");
/** api version */
define("VERSION", "v3.1");
/** API timeout*/
define("API_TIMEOUT", 10);
require("util.php");

/**
 * 反垃圾请求接口简单封装
 * $params 请求参数
 */
function check($params){
	$params["secretId"] = SECRETID;
	$params["businessId"] = BUSINESSID;
	$params["version"] = VERSION;
	$params["timestamp"] = time() * 1000;// time in milliseconds
	$params["nonce"] = sprintf("%d", rand()); // random int

	$params = toUtf8($params);
	$params["signature"] = gen_signature(SECRETKEY, $params);
	// var_dump($params);

    $result = curl_post($params, API_URL, API_TIMEOUT);
	if($result === FALSE){
		return array("code"=>500, "msg"=>"file_get_contents failed.");
	}else{
		return json_decode($result, true);	
	}
}

// 简单测试
function main(){
	$ret = array(
		"url"=>"http://xxx.xxx.com/xxxx",
		"code"=>200
	);

	printf("reponse:</br>" . json_encode($ret));
    if ($ret["code"] == 200) {
        $status = $ret["result"]["status"];
        $taskId = $ret["result"]["taskId"];
        $reasonType = $ret["result"]["reasonType"];
        printf("</br>taskId=%s,实证认证结果:%s,原因详情:%s,具体请参考接口文档说明", $taskId, $status, $reasonType);
    } else {
        printf("</br>请求错误,请检查参数或重试");
	}
}

main();