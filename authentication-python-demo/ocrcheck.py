#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""易盾身份认证OCR信息查询接口python示例代码
接口文档: http://dun.163.com/api.html
python版本：python2.7
运行:
    1. 修改 SECRET_ID,SECRET_KEY,BUSINESS_ID 为对应申请到的值
    2. $ python ocr_check_api_demo.py
"""
__author__ = 'yidun-dev'
__version__ = '0.1-dev'

import hashlib
import time
import random
import urllib
import urllib2
import json

class TextCheckAPIDemo(object):
    """易盾身份认证OCR信息查询接口接口示例代码"""
    API_URL = "https://verify.dun.163yun.com/v1/ocr/check"
    VERSION = "v1"

    def __init__(self, secret_id, secret_key, business_id):
        """
        Args:
            secret_id (str) 产品密钥ID，产品标识
            secret_key (str) 产品私有密钥，服务端生成签名信息使用
            business_id (str) 业务ID，易盾根据产品业务特点分配
        """
        self.secret_id = secret_id
        self.secret_key = secret_key
        self.business_id = business_id

    def gen_signature(self, params=None):
        """生成签名信息
        Args:
            params (object) 请求参数
        Returns:
            参数签名md5值
        """
        buff = ""
        for k in sorted(params.keys()):
            buff += str(k)+ str(params[k])
        buff += self.secret_key
        return hashlib.md5(buff).hexdigest()

    def check(self, params):
        """请求易盾接口
        Args:
            params (object) 请求参数
        Returns:
            请求结果，json格式
        """
        params["secretId"] = self.secret_id
        params["businessId"] = self.business_id
        params["version"] = self.VERSION
        params["timestamp"] = int(time.time() * 1000)
        params["nonce"] = int(random.random()*100000000)
        params["signature"] = self.gen_signature(params)

        try:
            params = urllib.urlencode(params)
            request = urllib2.Request(self.API_URL, params)
            content = urllib2.urlopen(request, timeout=1).read()
            return json.loads(content)
        except Exception, ex:
            print "调用API接口失败:", str(ex)

if __name__ == "__main__":
    """示例代码入口"""
    SECRET_ID = "your_secret_id" # 产品密钥ID，产品标识
    SECRET_KEY = "your_secret_key" # 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露
    BUSINESS_ID = "your_business_id" # 业务ID，易盾根据产品业务特点分配
    text_api = TextCheckAPIDemo(SECRET_ID, SECRET_KEY, BUSINESS_ID)

    params = {
        "picType": "1",
        "frontPicture": "http://www.xxx.com/xxx.jpg",
        "backPicture": "http://www.xxx.com/xxx.jpg",
    }
    ret = text_api.check(params)
    if ret is not None:
        if ret["code"] == 200:
            status = ret["result"]["status"]
            taskId = ret["result"]["taskId"]
            responsedetail = ret['result']['ocrResponseDetail']
            if status == 1:
                ocrName = ""
                ocrCardNo = ""
                expireDate = ""
                birthDay = ""
                nation = ""
                authority = ""
                gender = ""
                ocrAvatar = ""
                if responsedetail["ocrCardNo"] is not None:
                    ocrCardNo = responsedetail["ocrCardNo"].encode("utf-8")
                if responsedetail['ocrName'] is not None:
                    ocrName = responsedetail['ocrName'].encode("utf-8")
                if responsedetail['expireDate'] is not None:
                    expireDate = responsedetail['expireDate'].encode("utf-8")
                if responsedetail['birthDay'] is not None:
                    birthDay = responsedetail['birthDay'].encode("utf-8")
                if responsedetail['nation'] is not None:
                    nation = responsedetail['nation'].encode("utf-8")
                if responsedetail['gender'] is not None:
                    gender = responsedetail['gender'].encode("utf-8")
                if responsedetail['ocrAvatar'] is not None:
                    ocrAvatar = responsedetail['ocrAvatar'].encode("utf-8")
                if responsedetail['authority'] is not None:
                    authority = responsedetail['authority'].encode("utf-8")
                print "taskId=%s，查询成功,name = %s , cardNo = %s , expireDate = %s ,gender = %s ,nation = %s,birthDay = %s,ocrAvatar=%s,authority=%s" \
                    % (taskId.encode("utf-8"), ocrName, ocrCardNo, expireDate,gender, nation, birthDay,ocrAvatar,authority)
            elif status == 2:
                print "taskId=%s，解析失败,上传图片为非身份证图片或识别失败" % taskId.encode("utf-8")
        else:
            print "ERROR: ret.code=%s" %(ret['code'])