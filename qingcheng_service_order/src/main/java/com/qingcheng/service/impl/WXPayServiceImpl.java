package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.Config;
import com.github.wxpay.sdk.WXPayRequest;
import com.github.wxpay.sdk.WXPayUtil;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.order.WXPayService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Super_DH
 * The ideal is to be authentic but larger than life.
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-22    20:22
 */
@Service
public class WXPayServiceImpl implements WXPayService {

    @Autowired
    private Config config;

    @Reference
    private OrderService orderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 生成微信支付二维码
     *
     * @param orderId   订单查询
     * @param money     金额(分)
     * @param notifyUrl 回调地址
     * @return
     */
    @Override
    public Map createNative(String orderId, Integer money, String notifyUrl) {
        try {
            // 1.创建参数
            Map<String, String> param = new HashMap();//创建参数
            param.put("appid", config.getAppID());
            param.put("mch_id", config.getMchID());
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            param.put("body", "华哥哥小店耶");
            param.put("out_trade_no", orderId);//商户订单号
            param.put("total_fee", money + "");
            param.put("spbill_create_ip", "127.0.0.1");
            param.put("notify_url", notifyUrl);
            param.put("trade_type", "NATIVE");
            String xmlParam = WXPayUtil.generateSignedXml(param, config.getKey());
            System.out.println("参数：" + xmlParam);

            // 2.发送请求
            WXPayRequest wxPayRequest = new WXPayRequest(config);
            String result = wxPayRequest.requestWithCert("/pay/unifiedorder", null, xmlParam, false);

            // 3.封装结果
            System.out.println(result);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            HashMap<String, String> map = new HashMap<>();
            map.put("code_url", resultMap.get("code_url"));
            map.put("total_fee", money + "");
            map.put("out_trade_no", orderId);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }

    }

    /**
     * 通知判断签名是否正确
     *
     * @param xml
     */
    @Override
    public void notifyLogic(String xml) {

        try {
            // 1.对xml进行解析Map
            Map<String, String> map = WXPayUtil.xmlToMap(xml);
            // 2.验证签名
            boolean signatureValid = WXPayUtil.isSignatureValid(map, config.getKey());
            System.out.println("验证签名是否正确:" + signatureValid);
            System.out.println(map.get("out_trade_no"));
            System.out.println(map.get("result_code"));

            if (signatureValid) {
                if ("SUCCESS".equals(map.get("result_code"))) {
                    // 修改订单状态
                    orderService.updatePayStatus(map.get("out_trade_no"), map.get("transaction_id"));
                    // 存入rabbitMQ中返回数据
                    rabbitTemplate.convertAndSend("paynotify","",map.get("out_trade_no"));
                }else{
                    // 记录日志
                }
            }else {
                // 记录日志
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
