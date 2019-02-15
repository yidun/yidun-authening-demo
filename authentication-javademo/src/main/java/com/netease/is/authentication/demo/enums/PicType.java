/*
 * @(#) PicType.java 2017年4月19日
 * 
 * Copyright 2010 NetEase.com, Inc. All rights reserved.
 */
package com.netease.is.authentication.demo.enums;



/**
 *
 * @author hzdingyong
 * @version 2017年4月19日
 */
public enum PicType {
    URL(1),
    BASE64(2);

    private int type;

    private PicType(int type) {
        this.type = type;
    }

    public static boolean isValidType(int type) {
        if (type != 1 && type != 2) {
            return false;
        }
        return true;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
