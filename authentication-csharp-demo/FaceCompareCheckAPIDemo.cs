using System;
using System.Collections.Generic;
using System.Net.Http;
using Newtonsoft.Json.Linq;

/**
 * 人脸比对接口示例代码
 * 接口文档: https://support.dun.163.com/documents/391676076156063744?docId=456323166182072320
 */

namespace Com.Netease.Is.Authentication.Demo
{
    class FaceCompareCheckAPIDemo
    {
        public static void test()
        {
            /** 产品密钥ID，产品标识 */
            String secretId = "your_secretId";
            /** 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露 */
            String secretKey = "your_secretKey";
            /** 业务ID，易盾根据产品业务特点分配 */
            String businessId = "your_businessId";
            /** 验证接口地址 */
            String apiUrl = "https://verify.dun.163.com/v1/facecompare/check";

            Dictionary<String, String> parameters = new Dictionary<String, String>();
            long curr = (long)(DateTime.UtcNow - new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc)).TotalMilliseconds;
            String time = curr.ToString();

            // 1.设置公共参数
            parameters.Add("secretId", secretId);
            parameters.Add("businessId", businessId);
            parameters.Add("version", "v1");
            parameters.Add("timestamp", time);
            parameters.Add("nonce", new Random().Next().ToString());

            // 2.设置私有参数
            parameters.Add("picType", "1");
            parameters.Add("avatar1", "http://123.jpg");
            parameters.Add("avatar2", "http://123.jpg");
            parameters.Add("dataId", "323311889");

            // 3.生成签名信息
            String signature = Utils.genSignature(secretKey, parameters);
            parameters.Add("signature", signature);

            // 4.发送HTTP请求
            HttpClient client = Utils.makeHttpClient();
            String result = Utils.doPost(client, apiUrl, parameters, 10000);
            if (result != null)
            {
                JObject ret = JObject.Parse(result);
                int code = ret.GetValue("code").ToObject<Int32>();
                String msg = ret.GetValue("msg").ToObject<String>();
                if (code == 200)
                {
                    Console.WriteLine(ret.GetValue("result").ToString());
                }
                else
                {
                    Console.WriteLine(String.Format("ERROR: code={0}, msg={1}", code, msg));
                }
            }
            else
            {
                Console.WriteLine("Request failed!");
            }

        }
    }
}