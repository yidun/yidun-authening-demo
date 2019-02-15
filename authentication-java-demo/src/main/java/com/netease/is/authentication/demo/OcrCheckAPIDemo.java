/*
 * @(#) ImageCheckAPIDemo.java 2016年3月15日
 *
 * Copyright 2010 NetEase.com, Inc. All rights reserved.
 */
package com.netease.is.authentication.demo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netease.is.authentication.demo.enums.PicType;
import com.netease.is.authentication.demo.utils.HttpClient4Utils;
import com.netease.is.authentication.demo.utils.SignatureUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 1. httpclient，用于发送http请求
 * 2. commons-codec，使用md5算法生成签名信息，详细见SignatureUtils.java
 * 3. gson，用于做json解析
 *
 */
public class OcrCheckAPIDemo {
    /** 产品密钥ID，产品标识 */
    private final static String SECRETID = "your_secret_id";
    /** 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露 */
    private final static String SECRETKEY = "your_secret_key";
    /** 业务ID，易盾根据产品业务特点分配 */
    private final static String BUSINESSID = "your_business_id";
    /**接口地址 */
    private final static String API_URL = "httpS://verify.dun.163yun.com/v1/ocr/check";
    /** 实例化HttpClient，发送http请求使用，可根据需要自行调参 */
    private static HttpClient httpClient = HttpClient4Utils.createHttpClient(100, 20, 10000, 2000, 2000);

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Map<String, String> params = buildParam();
        String response = HttpClient4Utils.sendPost(httpClient, API_URL, params, Consts.UTF_8);
        JsonObject jObject = new JsonParser().parse(response).getAsJsonObject();
        int code = jObject.get("code").getAsInt();
        String msg = jObject.get("msg").getAsString();
        if (code == 200) {
            JsonObject resultObject = jObject.getAsJsonObject("result");
            int status = resultObject.get("status").getAsInt();
            String taskId = resultObject.get("taskId").getAsString();
            if (status == 1) {
                JsonObject responseDetail = resultObject.get("ocrResponseDetail").getAsJsonObject();
                if(responseDetail != null){
                    String ocrName = "";
                    String ocrCardNo = "";
                    String expireDate = "";
                    String gender = "";
                    String birthday = "";
                    String nation = "";
                    String authority = "";
                    String ocrAvatar = "";
                    JsonElement ocrNameEle = responseDetail.get("ocrName");
                    if(!ocrNameEle.isJsonNull()) {
                        ocrName  =ocrNameEle.getAsString();
                    }

                    JsonElement ocrCardElement = responseDetail.get("ocrCardNo");
                    if(!ocrCardElement.isJsonNull()) {
                        ocrName  =ocrCardElement.getAsString();
                    }

                    JsonElement expireDateEle = responseDetail.get("expireDate");
                    if(!expireDateEle.isJsonNull()) {
                        expireDate  =expireDateEle.getAsString();
                    }

                    JsonElement genderEle = responseDetail.get("gender");
                    if (!genderEle.isJsonNull()) {
                        gender = genderEle.getAsString();
                    }

                    JsonElement nationEle = responseDetail.get("nation");
                    if (!nationEle.isJsonNull()) {
                        nation = nationEle.getAsString();
                    }

                    JsonElement birthdayEle = responseDetail.get("birthday");
                    if (!birthdayEle.isJsonNull()) {
                        birthday = birthdayEle.getAsString();
                    }

                    JsonElement ocrAvatarEle = responseDetail.get("ocrAvatar");
                    if (!ocrAvatarEle.isJsonNull()) {
                        ocrAvatar = ocrAvatarEle.getAsString();
                    }

                    JsonElement authorityEle = responseDetail.get("authority");
                    if (!authorityEle.isJsonNull()) {
                        authority = authorityEle.getAsString();
                    }

                    System.out.println(String.format("taskId=%s，接口查询成功,name = %s,cardNo = %s , " +
                                    "expireDate = %s,gender = %s , nation = %s , birthday = %s , ocrAvatar = %s , authority = %s",
                            taskId,ocrName,ocrCardNo,expireDate, gender, nation, birthday, ocrAvatar, authority));
                }

            } else if (status == 2) {
                System.out.println(String.format("taskId=%s，解析失败,上传图片为非身份证图片", taskId));
            }
        } else {
            System.out.println(String.format("ERROR: code=%s, msg=%s", code, msg));
        }
    }

    private static Map<String, String> buildParam() throws IOException {
        final Map<String, String> params =  new HashMap<String, String>();
        params.put("secretId", SECRETID);
        params.put("businessId", BUSINESSID);
        params.put("nonce", String.valueOf(RandomUtils.nextLong(10000, 10000000)));
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("version", "v1");
        params.put("picType", String.valueOf(PicType.URL.getType()));
        params.put("frontPicture", "http://nos.netease.com/testopen-image/1505896184650.jpeg");
        params.put("backPicture", "http://nos.netease.com/testopen-image/1501039268055.jpg");
        String signature = SignatureUtils.genSignature(SECRETKEY, params);
        params.put("signature", signature);
        return params;
    }

}
