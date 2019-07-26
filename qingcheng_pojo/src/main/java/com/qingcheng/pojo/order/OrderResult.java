package com.qingcheng.pojo.order;

import java.io.Serializable;
import java.util.List;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-06-27    10:07
 */

/**
 * 返回order的值和订单明细
 */
public class OrderResult implements Serializable {

    private Order order;

    private List<OrderItem> orderItemsList;

    @Override
    public String toString() {
        return "OrderResult{" +
                "order=" + order +
                ", orderItemsList=" + orderItemsList +
                '}';
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItemsList() {
        return orderItemsList;
    }

    public void setOrderItemsList(List<OrderItem> orderItemsList) {
        this.orderItemsList = orderItemsList;
    }
}
