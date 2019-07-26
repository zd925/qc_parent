package com.qingcheng.service.order;

import java.util.Map;

/**
 * Created by Super_DH
 * The ideal is to be authentic but larger than life.
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-22    20:17
 */
public interface WXPayService {

    /**
     * 生成微信支付二维码
     * @param orderId 订单查询
     * @param money 金额(分)
     * @param notifyUrl 回调地址
     * @return
     */
    public Map createNative(String orderId,Integer money,String notifyUrl);

    /**
     *  微信支付回调
     * @param xml
     */
    public void notifyLogic(String xml);



}
