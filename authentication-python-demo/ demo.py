#!/usr/bin/env python3

import requests
import json

data = {
    'no': 1,
    'name': 'Runoob',
    'url': 'http://www.runoob.com'
}
strr = '''
{
    "REQUEST": "FOUND",
    "739c5b1cd5681e668f689aa66bcc254c": {
        "plain": "test",
        "hexplain": "74657374",
        "algorithm": "MD5X5PLAIN"
    }
}
'''
#json_str = json.dumps(strr)
result = json.loads(strr)
result1 = result.get("739c5b1cd5681e668f689aa66bcc254c")
print(result.get("code1"))
print(result1.get("739c5b1cd5681e668f689aa66bcc254c"))
print(result1.get("plain"))

# print(data.get("result"))
# print(data["name"])
# print(type(data))
# r = requests.post('https://verify.dun.163.com/v1/bankcard/check')
# result = json.loads(r.text)
# print(type(result))
# print(result.get("code"))
# print(result.get("code1"))

# print(json_str.)
