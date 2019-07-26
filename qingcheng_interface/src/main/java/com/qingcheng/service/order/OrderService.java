package com.qingcheng.service.order;

import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.pojo.order.OrderResult;

import java.util.List;
import java.util.Map;

/**
 * order业务逻辑层
 */
public interface OrderService {

    /**
     * 未付款订单取消
     */
    public void orderTimeOutLogic();

    /**
     * 根据id查询订单明细
     *
     * @param id
     * @return
     */
    public OrderResult findOrderResultById(String id);

    public List<Order> findAll();


    public PageResult<Order> findPage(int page, int size);


    public List<Order> findList(Map<String, Object> searchMap);


    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size);


    public Order findById(String id);

    public Map<String, Object> add(Order order);


    public void update(Order order);


    public void delete(String id);

    /**
     * 订单发货
     *
     * @return
     */
    List<Order> findOrderListByIds(Map<String, String> searchMap);

    void batchSend(List<Order> orders);

    void split(List<OrderItem> list);

    void merge(String order1, String order2);

    /**
     *  修改订单状态
     * @param orderId
     * @param transactionId
     */
    public void updatePayStatus(String orderId,String transactionId);

}
