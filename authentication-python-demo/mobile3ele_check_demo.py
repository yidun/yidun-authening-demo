#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
手机号三要素认证接口示例代码
接口文档: https://support.dun.163.com/documents/391676076156063744?docId=391677761079283712
python版本：python3.x
运行:
    1. 修改 SECRET_ID,SECRET_KEY,BUSINESS_ID 为对应申请到的值
    2. $ python3 xxx.py
"""
__author__ = 'yidun-dev'

import hashlib
import time
import random
import requests
import json
import sys


class TextCheckAPIDemo(object):
    API_URL = "https://verify.dun.163.com/v1/mobile/check"
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
            buff += str(k) + str(params[k])
        buff += self.secret_key
        return hashlib.md5(buff.encode("utf-8")).hexdigest()

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
            print(params)
            return requests.post(self.API_URL, params, headers={'Content-Type': 'application/x-www-form-urlencoded'})
        except BaseException as e:
            print("调用API接口失败:", e)


if __name__ == "__main__":
    """示例代码入口"""
    SECRET_ID = "your_secret_id"  # 产品密钥ID，产品标识
    SECRET_KEY = "your_secret_key"  # 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露
    BUSINESS_ID = "your_business_id"  # 业务ID，易盾根据产品业务特点分配
    text_api = TextCheckAPIDemo(SECRET_ID, SECRET_KEY, BUSINESS_ID)

    params = {
        "name": "张三",
        "cardNo": "123456908776554311",
        "phone": "13145678911"
    }
    ret = text_api.check(params)
    status_code = ret.status_code
    if status_code != 200:
        print("请求错误,响应状态码: %s,请检查请求参数或重试" % status_code)
        sys.exit(0)

    response = json.loads(ret.text)
    code = response.get("code")
    if code == 200:
        result = response.get("result")
        print("taskId=%s,手机号三要素认证结果:%s,原因详情:%s,具体请参考接口文档说明" %
              (result.get("taskId"), result.get("status"), result.get("reasonType")))
    else:
        print("请求错误: %s,请检查参数或重试" % response)
