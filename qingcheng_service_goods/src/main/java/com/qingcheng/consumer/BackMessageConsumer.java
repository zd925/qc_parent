package com.qingcheng.consumer;


import com.alibaba.fastjson.JSON;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.StockBackService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-21    19:57
 */
@Component("messageConsumer")
public class BackMessageConsumer implements MessageListener {

    @Autowired
    private StockBackService stockBackService;

    @Override
    public void onMessage(Message message) {
        try {
            String jsonString = new String(message.getBody());
            List<OrderItem> orderItems = JSON.parseArray(jsonString, OrderItem.class);
            stockBackService.addList(orderItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
