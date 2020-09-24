/*
 * @(#) ImageCheckAPIDemo.java 2016年3月15日
 *
 * Copyright 2010 NetEase.com, Inc. All rights reserved.
 */
package com.netease.is.authentication.demo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netease.is.authentication.demo.utils.HttpClient4Utils;
import com.netease.is.authentication.demo.utils.SignatureUtils;
import org.apache.http.client.HttpClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 人脸检测 接口调用示例,该示例依赖以下jar包：
 * 1. httpclient，用于发送http请求,详细见HttpClient4Utils.java
 * 2. commons-codec，使用md5算法生成签名信息，详细见SignatureUtils.java
 * 3. gson，用于做json解析
 *
 * @author yidun
 */
public class FaceDetectCheckAPIDemo {
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
     * 接口地址
     */
    private final static String API_URL = "https://verify.dun.163yun.com/v1/facedetect/check";
    /**
     * 实例化HttpClient，发送http请求使用，可根据需要自行调参
     */
    private static HttpClient httpClient = HttpClient4Utils.createHttpClient(100, 20, 10000, 2000, 2000);


    public static void main(String[] args) throws Exception {
        Map<String, String> params = new HashMap<>(10);
        // 1.设置公共参数
        params.put("secretId", SECRET_ID);
        params.put("businessId", BUSINESS_ID);
        params.put("version", "v1");
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("nonce", String.valueOf(new Random().nextInt()));

        // 2.设置私有参数
        params.put("picType", "1");
        params.put("picImage", "http://xxxx.jpg");

        // 3.生成签名信息
        String signature = SignatureUtils.genSignature(SECRET_KEY, params);
        params.put("signature", signature);

        // 4.发送HTTP请求，这里使用的是HttpClient工具包，产品可自行选择自己熟悉的工具包发送请求
        String response = HttpClient4Utils.sendPost(httpClient, API_URL, params);

        //5.解析报文返回
        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
        System.out.printf("response=%s%n", jsonObject);
        int code = jsonObject.get("code").getAsInt();
        if (code == 200) {
            JsonObject resultObject = jsonObject.getAsJsonObject("result");
            String requestId = resultObject.get("requestId").getAsString();
            int status = resultObject.get("status").getAsInt();
            if (status == 1) {
                int faceNumber = resultObject.get("faceNumber").getAsInt();
                JsonArray faceLocations = resultObject.getAsJsonArray("faceLocations");
                System.out.printf("requestId=%s,人脸数量:%s,人脸位置信息：%s%n", requestId, faceNumber, faceLocations);
            } else {
                System.out.printf("requestId=%s，检测失败,原因多为图片下载失败%n", requestId);
            }
        } else {
            System.out.printf("ERROR: response=%s%n", jsonObject);
        }
    }
}
