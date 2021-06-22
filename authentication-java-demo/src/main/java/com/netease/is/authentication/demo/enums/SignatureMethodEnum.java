/*
 * @(#) SignatureMethodEnum.java 2021-06-11
 *
 * Copyright 2021 NetEase.com, Inc. All rights reserved.
 */

package com.netease.is.authentication.demo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author yidun
 * @version 2021-06-11
 */
public enum SignatureMethodEnum {
    MD5,
    SHA1,
    SHA256,
    SM3;

    public static boolean isValid(String signatureMethod) {
        try {
            SignatureMethodEnum signatureMethodEnum = SignatureMethodEnum.valueOf(StringUtils.upperCase(signatureMethod));
            return signatureMethodEnum != null;
        } catch (Exception e) {
            return false;
        }
    }
}