/**
 * 人脸比对接口示例代码
 * 接口文档: https://support.dun.163.com/documents/391676076156063744?docId=456323166182072320
 */

var utils = require("./utils");
//产品密钥ID，产品标识 
var secretId = "your_secretId";
// 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露 
var secretKey = "your_secretKey";
// 业务ID，易盾根据产品业务特点分配 
var businessId = "your_businessId";
// 易盾反垃圾云服务直播流信息提交接口地址 
var apiurl = "https://verify.dun.163.com/v1/facecompare/check";
//请求参数
var post_data = {
	// 1.设置公有有参数
	secretId: secretId,
	businessId: businessId,
	version: "v1",
	timestamp: new Date().getTime(),
	nonce: utils.noncer(),
	signatureMethod: "MD5",
	// 2.设置私有参数
	picType: "1",
    avatar1: "http://123.jpg",
    avatar2: "http://123.jpg",
    dataId: "323311889"
};
var signature = utils.genSignature(secretKey, post_data);
post_data.signature = signature;
//http请求结果
var responseCallback = function (responseData) {
	console.log(responseData);
	var data = JSON.parse(responseData);
	var code = data.code;
	var msg = data.msg;
	if (code == 200) {
		var result = data.result;
		console.log("requestId=%s,人脸比对结果:%s,相似度得分:%s,具体请参考接口文档说明",result.requestId,result.matched,result.score);
	} else {
		console.log('ERROR:code=' + code + ',msg=' + msg);
	}

}
utils.sendHttpRequest(apiurl, "POST", post_data, responseCallback);