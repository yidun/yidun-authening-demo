/**
 * 易盾工具集合，包含：
 * 1：noncer 产生随机整数
 * 2：genSignature 生成Md5签名
 * 3:sendHttpRequest 发送http请求
 */
var http = require('http');
var urlutil=require('url');
var querystring = require('querystring');
var crypto = require('crypto');
var sm3 = require("sm3");
//产生随机整数--工具方法
var noncer=function(){
	var range=function(start,end){
		var array=[];
		for(var i=start;i<end;++i){
			array.push(i);
		}
		return array;
	};
	var nonce = range(0,6).map(function(x){
		return Math.floor(Math.random()*10);
	}).join('');
	return nonce;
};
//生成签名算法--工具方法
var genSignature=function(secretKey,paramsJson){
	var sorter=function(paramsJson){
		var sortedJson={};
		var sortedKeys=Object.keys(paramsJson).sort();
		for(var i=0;i<sortedKeys.length;i++){
			sortedJson[sortedKeys[i]] = paramsJson[sortedKeys[i]]
		}
		return sortedJson;
	}
	var sortedParam=sorter(paramsJson);
	var needSignatureStr="";
	for(var key in sortedParam){
		var value=sortedParam[key];
		needSignatureStr=needSignatureStr+key+value;
	}
	needSignatureStr+=secretKey;
	var signatureMethod = paramsJson.signatureMethod;
	if (signatureMethod == undefined || signatureMethod == null) {
		signatureMethod = "md5";
	}
	signatureMethod = signatureMethod.toLowerCase();
	switch (signatureMethod) {
		case "md5":
		case "sha1":
		case "sha256":
			return crypto.createHash(signatureMethod).update(needSignatureStr, "utf-8").digest("hex");
		case "sm3":
			return sm3(needSignatureStr);
		default:
			console.log("[ERROR] 签名方法不支持");
			return null;
	}
};
//发送post请求
var sendHttpRequest=function(url,type,data,callback){
	var content = querystring.stringify(data,null,null,null);
	var urlObj=urlutil.parse(url);
	var host=urlObj.hostname;
	var path=urlObj.path;
	var port=urlObj.port;
	var options = {
			hostname: host,
			port: port,
			path: path,
			method: type,
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
				'Content-Length': Buffer.byteLength(content)
			}
		};
	var responseData="";
	var req = http.request(options, function (res) {
		res.setEncoding('utf8');
		res.on('data', function (chunk) {
			responseData+=chunk;
		});
		res.on('end', function () {
			callback(responseData);
		});
		//设置超时
		req.setTimeout(5000,function(){
			console.log('request timeout!');
			req.abort();
		});
		req.on('error', function (e) {
			console.log('request ERROR: ' + e.message);
		});
	});
	req.write(content);
	req.end();
};

exports.noncer=noncer;
exports.genSignature=genSignature;
exports.sendHttpRequest=sendHttpRequest;