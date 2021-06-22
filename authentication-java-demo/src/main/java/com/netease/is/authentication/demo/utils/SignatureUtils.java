/*
 * @(#) SignatureUtils.java 2016年2月2日
 * 
 * Copyright 2010 NetEase.com, Inc. All rights reserved.
 */
package com.netease.is.authentication.demo.utils;

import com.netease.is.authentication.demo.enums.SignatureMethodEnum;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.digests.SM3Digest;

/**
 * 生成及验证签名信息工具类
 * @author hzgaomin
 * @version 2016年2月2日
 */
public class SignatureUtils {

    /**
     * 生成签名信息
     * @param secretKey 产品私钥
     * @param params 接口请求参数名和参数值map，不包括signature参数名
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getSignature(String secretKey, Map<String, String> params) throws UnsupportedEncodingException {
        return getSignature(secretKey, params, params.get("signatureMethod"));
    }

    /**
     * 生成签名信息
     * @param secretKey 产品私钥
     * @param params 接口请求参数名和参数值map，不包括signature参数名
     * @param signatureMethod 加密方法
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getSignature(String secretKey, Map<String, String> params, String signatureMethod) throws UnsupportedEncodingException {
        // 1. 参数名按照ASCII码表升序排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 2. 按照排序拼接参数名与参数值
        StringBuffer paramBuffer = new StringBuffer();
        for (String key : keys) {
            paramBuffer.append(key).append(params.get(key) == null ? "" : params.get(key));
        }
        // 3. 将secretKey拼接到最后
        paramBuffer.append(secretKey);

        if (StringUtils.isEmpty(signatureMethod)) {
            // 4. MD5是128位长度的摘要算法，用16进制表示，一个十六进制的字符能表示4个位，所以签名后的字符串长度固定为32个十六进制字符。
            return DigestUtils.md5Hex(paramBuffer.toString().getBytes("UTF-8"));
        } else {
            switch (SignatureMethodEnum.valueOf(StringUtils.upperCase(signatureMethod))) {
                case MD5:
                    return DigestUtils.md5Hex(paramBuffer.toString().getBytes("UTF-8"));
                case SHA1:
                    return DigestUtils.sha1Hex(paramBuffer.toString().getBytes("UTF-8"));
                case SHA256:
                    return DigestUtils.sha256Hex(paramBuffer.toString().getBytes("UTF-8"));
                case SM3:
                    return sm3DigestHex(paramBuffer.toString().getBytes("UTF-8"));
                default:
                    return "";
            }
        }
        return "";
    }

    public static String sm3DigestHex(byte[] srcData) throws UnsupportedEncodingException {
        SM3Digest sm3Digest = new SM3Digest();
        sm3Digest.update(srcData, 0, srcData.length);
        byte[] hash = new byte[sm3Digest.getDigestSize()];
        sm3Digest.doFinal(hash, 0);
        return Hex.encodeHexString(hash);
    }

}
