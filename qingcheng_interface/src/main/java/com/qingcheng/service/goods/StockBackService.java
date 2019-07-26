package com.qingcheng.service.goods;

import com.qingcheng.pojo.order.OrderItem;

import java.util.List;

/**
 * Created by Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-21    19:53
 */
public interface StockBackService {

    public  void addList(List<OrderItem> orderItems);

    public void doBack();

}
