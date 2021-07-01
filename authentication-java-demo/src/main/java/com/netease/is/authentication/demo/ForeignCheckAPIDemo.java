/*
 * @(#) ImageCheckAPIDemo.java 2016年3月15日
 *
 * Copyright 2010 NetEase.com, Inc. All rights reserved.
 */
package com.netease.is.authentication.demo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netease.is.authentication.demo.utils.HttpClient4Utils;
import com.netease.is.authentication.demo.utils.SignatureUtils;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 出入境证件核验 接口调用示例,该示例依赖以下jar包：
 * 1. httpclient，用于发送http请求,详细见HttpClient4Utils.java
 * 2. commons-codec，使用md5算法生成签名信息，详细见SignatureUtils.java
 * 3. gson，用于做json解析
 *
 * @author yidun
 */
public class ForeignCheckAPIDemo {
    /**
     * 产品密钥ID，产品标识
     */
    private final static String SECRET_ID = "your_secret_id";
    /**
     * 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露
     */
    private final static String SECRET_KEY = "your_secret_key";
    /**
     * 业务ID，易盾根据产品业务特点分配
     */
    private final static String BUSINESS_ID = "your_business_id";
    /**
     * 易盾身份认证服务出入境证件核验在线检测接口地址
     */
    private final static String API_URL = "https://verify.dun.163.com/v1/foreign/check";
    /**
     * 实例化HttpClient，发送http请求使用，可根据需要自行调参
     */
    private static HttpClient httpClient = HttpClient4Utils.createHttpClient(100, 20, 5000, 2000, 2000);


    public static void main(String[] args) throws Exception {
        Map<String, String> params = new HashMap<>(8);
        // 1.设置公共参数
        params.put("secretId", SECRET_ID);
        params.put("businessId", BUSINESS_ID);
        params.put("version", "v1");
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("nonce", String.valueOf(new Random().nextInt()));
        // params.put("signatureMethod", "SM3"); // 可选参数，可选值为MD5、SHA1、SHA256、SM3。如果不传该字段，默认采用MD5算法生成签名。

        // 2.设置私有参数
        params.put("name", "张三");
        params.put("cardNo", "123456789012345678");
        params.put("verifyType", "1");
        params.put("nation", "CHN");



        // 3.生成签名信息
        String signature = SignatureUtils.genSignature(SECRET_KEY, params);
        params.put("signature", signature);

        // 4.发送HTTP请求，这里使用的是HttpClient工具包，产品可自行选择自己熟悉的工具包发送请求
        String response = HttpClient4Utils.sendPost(httpClient, API_URL, params);

        // 5.解析接口返回值
        JsonObject jObject = new JsonParser().parse(response).getAsJsonObject();
        int code = jObject.get("code").getAsInt();
        String msg = jObject.get("msg").getAsString();
        if (code == 200) {
            JsonObject resultObject = jObject.getAsJsonObject("result");
            int status = resultObject.get("status").getAsInt();
            String taskId = resultObject.get("taskId").getAsString();
            int reasonType = resultObject.get("reasonType").getAsInt();
            if (status == 1) {
                System.out.printf("taskId=%s，在线认证结果：通过%n", taskId);
            } else if (status == 2) {
                System.out.printf("taskId=%s，在线认证结果：不通过, 原因：%s%n", taskId, reasonType);
            } else if (status == 0) {
                System.out.printf("taskId=%s，在线认证结果：待定%n", taskId);
            }else {
                System.out.printf("taskId=%s，status=%s，其他情况见接口文档说明%n", taskId, status);
            }
        } else {
            System.out.printf("ERROR: code=%s, msg=%s%n", code, msg);
        }

    }

}
