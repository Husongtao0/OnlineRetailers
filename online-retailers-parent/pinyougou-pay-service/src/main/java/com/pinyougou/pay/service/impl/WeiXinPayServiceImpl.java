package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.pay.service.impl
 * @company www.itheima.com
 */
@Service(timeout = 10000)
public class WeiXinPayServiceImpl implements WeiXinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;


    @Override
    public Map createNative(String out_trade_no, String total_fee) {

        try {
            //1.设置参数（需要参数 微信要求的参数）
            Map<String,String> param = new HashMap();

            param.put("appid",appid);
            param.put("mch_id",partner);
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机生成的字符串
            param.put("body","品优购");
            param.put("out_trade_no",out_trade_no);//交易的订单号码
            param.put("total_fee",total_fee);
            param.put("spbill_create_ip","127.0.0.1");//
            param.put("notify_url","http://www.itheima.com");//必填，但是不用

            param.put("trade_type","NATIVE");//扫描支付
            //2.调用生成签名的API 返回的是一个参数集合
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //3.执行请求（调用统一下单的API）
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);//向 微信发送生成支付二维码的请求 携带了参数

            httpClient.post();//以post形式发送请求执行
            String content = httpClient.getContent();//获取响应的内容

            Map<String, String> resultStringMap = WXPayUtil.xmlToMap(content);//包含了所有的返回值信息

            System.out.println("返回的信息:"+content);
            //4.获取结果（从微信返回出来的）
            Map resultMap = new HashMap();
            resultMap.put("code_url",resultStringMap.get("code_url"));
            resultMap.put("out_trade_no",out_trade_no);
            resultMap.put("total_fee",total_fee);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map queryStatus(String out_trade_no) {
        try {
            //1.设置参数（需要参数 微信要求的参数）
            Map<String,String> param = new HashMap();

            param.put("appid",appid);
            param.put("mch_id",partner);
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机生成的字符串
            param.put("out_trade_no",out_trade_no);//交易的订单号码

            //2.调用生成签名的API 返回的是一个参数集合
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //3.执行请求（查询支付状态的接口）
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);//向 微信发送生成支付二维码的请求 携带了参数

            httpClient.post();//以post形式发送请求执行
            String content = httpClient.getContent();//获取响应的内容

            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);//包含了所有的返回值信息

            System.out.println("查询交易订单的结果:"+content);

            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map closePay(String out_trade_no) {
        try {
            //1.设置参数（需要参数 微信要求的参数）
            Map<String,String> param = new HashMap();

            param.put("appid",appid);
            param.put("mch_id",partner);
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机生成的字符串
            param.put("out_trade_no",out_trade_no);//交易的订单号码

            //2.调用生成签名的API 返回的是一个参数集合
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //3.执行请求（查询支付状态的接口）
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);//向 微信发送生成支付二维码的请求 携带了参数

            httpClient.post();//以post形式发送请求执行
            String content = httpClient.getContent();//获取响应的内容

            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);//包含了所有的返回值信息

            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }
}
