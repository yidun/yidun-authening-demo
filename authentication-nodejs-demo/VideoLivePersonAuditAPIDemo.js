﻿/**
 * 视频人脸核身接口示例代码
 * 接口文档: https://support.dun.163.com/documents/391676076156063744?docId=437385732480196608
 */

var utils = require("./utils");
//产品密钥ID，产品标识 
var secretId = "your_secretId";
// 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露 
var secretKey = "your_secretKey";
// 业务ID，易盾根据产品业务特点分配 
var businessId = "your_businessId";
// 易盾反垃圾云服务直播流信息提交接口地址 
var apiurl = "https://verify.dun.163.com/v1/liveperson/h5/audit";
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
	name: "张三",
	cardNo: "123402199711012345",
  actions: "[4]",
  videoType: "2",
  needAvatar: "false"
};
var signature = utils.genSignature(secretKey, post_data);
post_data.signature = signature;
post_data.actionVideos="['BASE编码后的视频文件']";
//http请求结果
var responseCallback = function (responseData) {
	console.log(responseData);
	var data = JSON.parse(responseData);
	var code = data.code;
	var msg = data.msg;
	if (code == 200) {
		var result = data.result;
		console.log("taskId=%s,认证结果:%s,原因详情:%s,图片类型:%s,抓取头像照片:%s,公安是否比对通过:%s,相似度得分:%s,具体请参考接口文档说明",
                    result.taskId,result.status,result.reasonType,result.picType, result.avatar,result.faceMatched, result.similarityScore);
	} else {
		console.log('ERROR:code=' + code + ',msg=' + msg);
	}

}
utils.sendHttpRequest(apiurl, "POST", post_data, responseCallback);