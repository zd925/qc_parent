package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.OrderConfigMapper;
import com.qingcheng.dao.OrderItemMapper;
import com.qingcheng.dao.OrderLogMapper;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.*;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.util.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderConfigMapper orderConfigMapper;

    @Autowired
    private CartService cartService;

    @Reference
    private SkuService skuService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public List<Order> findOrderListByIds(Map<String, String> searchMap) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        // 判断ids是否为null
        if (searchMap.get("ids") != null) {
            List idsList = JSON.parseObject(searchMap.get("ids"), List.class);
            System.out.println("buweinull");
            criteria.andIn("id", idsList);
        }
        criteria.andEqualTo("consignStatus", searchMap.get("consignStatus"));
        List<Order> list = orderMapper.selectByExample(example);

        return list;

    }

    /**
     * 批量发货
     *
     * @param orders 订单集合
     * @return
     */
    @Override
    public void batchSend(List<Order> orders) {
        for (Order order : orders) {
            // 判断运单号和物流公司是否为空
            if (order.getShippingCode() == null || order.getShippingName() == null) {
                throw new RuntimeException("请选择快递公司和填写快递单号");
            }
            // 修改订单
            order.setOrderStatus("3"); // 订单状态 已发货
            order.setConsignStatus("2"); // 发货状态 已发货
            order.setConsignTime(new Date()); // 发货时间
            orderMapper.updateByPrimaryKeySelective(order);
            // 记录订单日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setOperater("华哥哥");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderId(order.getId());
            orderLog.setOrderStatus(order.getOrderStatus());
            orderLog.setPayStatus(order.getPayStatus());
            orderLog.setConsignStatus(order.getConsignStatus());
            orderLog.setRemarks("华哥哥上传的");
            orderLogMapper.insert(orderLog);

        }


    }

    /**
     * 拆分订单
     *
     * @param orderItems
     */
    @Override
    @Transactional
    public void split(List<OrderItem> orderItems) {
        // 新订单明细集合
        List<OrderItem> newOrderItems = new ArrayList<>();
        // 记录主订单详情id
        String orderId = "";

        // 判断拆分数量是否超过本身原有数量
        // 主订单拆分总数量
        Integer totalNum = 0;
        for (OrderItem orderItem : orderItems) {
            totalNum += orderItem.getNum();
            OrderItem orderItem1 = orderItemMapper.selectByPrimaryKey(orderItem.getId());
            orderId = orderItem1.getOrderId();


        }
        Integer totalNum1 = orderMapper.selectByPrimaryKey(orderId).getTotalNum();
        if (totalNum > totalNum1) {
            throw new RuntimeException("拆分数量大于原有数量");
        }

        for (OrderItem orderItem : orderItems) {

            // 1.根据订单明细id查询订单明细数据
            OrderItem orderItem1 = orderItemMapper.selectByPrimaryKey(orderItem.getId());
            // 2.获取主订单详情
            orderId = orderItem1.getOrderId();
            // 3.new一个订单明细对象,用于分类
            OrderItem orderItemNew = new OrderItem();
            // 4.对象复制
            BeanUtils.copyProperties(orderItemNew, orderItem1);
            // 5.修改主订单明细数量和金额.总金额, 实付金额
            orderItem1.setNum(orderItem1.getNum() - orderItem.getNum());
            orderItem1.setMoney(orderItem1.getNum() * orderItem1.getPrice());
            orderItem1.setPayMoney(orderItem1.getNum() * orderItem1.getPrice() + orderItem1.getPostFee());
            // 6.更新主表数据库
            orderItemMapper.updateByPrimaryKeySelective(orderItem1);
            // 7.修改从订单明细数据
            orderItemNew.setNum(orderItem.getNum());// 设置拆分数据
            // 修改从订单明细总金额
            orderItemNew.setMoney(orderItemNew.getNum() * orderItemNew.getPrice());
            // 修改从订单明细实付金额
            orderItemNew.setPayMoney(orderItemNew.getPrice() + orderItemNew.getPostFee());
            newOrderItems.add(orderItemNew);
        }
        // 处理订单详情功能
        // 1.订单详情+明细 使用findOrderResultById方法查询
        OrderResult orderResultOld = findOrderResultById(orderId);
        // 2.复制订单详情对象
        Order newOrder = new Order();
        BeanUtils.copyProperties(newOrder, orderResultOld.getOrder());
        // 3.修改主订单详情数据
        int orderNum = 0;
        int orderMoney = 0;
        // 4.循环遍历主订单下订单明细
        for (OrderItem orderItem : orderResultOld.getOrderItemsList()) {
            orderNum += orderItem.getNum();// 记录总数量
            orderMoney += orderItem.getMoney();// 记录总付款金额
        }
        orderResultOld.getOrder().setTotalNum(orderNum);// 更新主订单详情总数量
        orderResultOld.getOrder().setPayMoney(orderMoney);// 根性主订单总付款金额
        // 5.跟新主订单数据
        orderMapper.updateByPrimaryKeySelective(orderResultOld.getOrder());
        // 6.修改从订单详情数据
        int newOrderNum = 0;
        int newOrderMoney = 0;
        newOrder.setId(idWorker.nextId() + "");
        // 7.for循环遍历从订单明细
        for (OrderItem orderItem : newOrderItems) {
            // 修改从订单明细Id
            orderItem.setId(idWorker.nextId() + "");
            newOrderNum += orderItem.getNum(); // 记录总数量
            newOrderMoney += orderItem.getPayMoney();// 记录总付款金额
            orderItem.setOrderId(newOrder.getId()); // 设置从订单详情关联
        }
        newOrder.setTotalNum(newOrderNum); // 更新从订单详情总数量
        newOrder.setPayMoney(newOrderMoney);
        // 新增从订单详情
        orderMapper.insert(newOrder);
        // 新增从订单明细
        for (OrderItem orderItem : newOrderItems) {
            // 插入从订单
            orderItemMapper.insert(orderItem);
        }


    }

    /**
     * 合并订单
     *
     * @param orderId1
     * @param orderId2
     */
    @Override
    @Transactional
    public void merge(String orderId1, String orderId2) {
        // 创建两个订单详情
        Order order1 = orderMapper.selectByPrimaryKey(orderId1);
        Order order2 = orderMapper.selectByPrimaryKey(orderId2);
        // 创建两个订单明细
        OrderItem orderItem1 = orderItemMapper.selectByPrimaryKey(orderId1);
        OrderItem orderItem2 = orderItemMapper.selectByPrimaryKey(orderId2);
        // 创建一个订单详情 用于封装数据
        Order order = new Order();
        order.setId(order1.getId());
        // 金钱总数
        order.setPayMoney(order1.getPayMoney() + order2.getPayMoney());
        // 保存主订单金钱
        orderMapper.updateByPrimaryKeySelective(order);
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(orderId2);
        // 删除从订单(逻辑删除)
        order2.setIsDelete(null);
        orderMapper.updateByPrimaryKeySelective(order2);
        // 创建一个订单明细 用于封装数据
        OrderItem orderItem3 = new OrderItem();
        // 订单数量总数
        orderItem3.setNum(orderItem2.getNum() + orderItem1.getNum());
        // 上传数据主订单
        orderItemMapper.updateByPrimaryKeySelective(orderItem3);

        // 记录订单日志
        OrderLog orderLog = new OrderLog();
        orderLog.setId(idWorker.nextId() + "");
        orderLog.setOperater("华哥哥");
        orderLog.setOperateTime(new Date());
        orderLog.setOrderId(orderId1);
        orderLog.setOrderStatus(order.getOrderStatus());
        orderLog.setPayStatus(order.getPayStatus());
        orderLog.setConsignStatus(order.getConsignStatus());
        orderLog.setRemarks("华哥哥上传的");
        orderLogMapper.insert(orderLog);


    }


    /**
     * 修改订单状态
     * @param orderId
     * @param transactionId
     */
    @Override
    @Transactional
    public void updatePayStatus(String orderId, String transactionId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order!=null && "0".equals(order.getPayStatus())){
            // 存在订单且状态为0
            order.setPayStatus("1");
            order.setOrderStatus("1");
            order.setUpdateTime(new Date());
            order.setTransactionId(transactionId);// 微信返回的交易流水号
            orderMapper.updateByPrimaryKeySelective(order);

            // 记录订单变动日志
            OrderLog orderLog=new OrderLog();
            orderLog.setId( idWorker.nextId()+"" );
            orderLog.setOperater("system");  // 系统
            orderLog.setOperateTime(new Date());// 当前日期
            orderLog.setOrderStatus("1");
            orderLog.setPayStatus("1");
            orderLog.setRemarks("支付流水号"+transactionId);
            orderLog.setOrderId(orderId);
            orderLogMapper.insert(orderLog);
        }

    }

    /**
     * 订单超时处理
     */
    @Override
    public void orderTimeOutLogic() {
        // 订单超时未支付 自动关闭
        // 查询超时时间
        OrderConfig orderConfig = orderConfigMapper.selectByPrimaryKey("1");
        // 超时时间60分钟
        Integer commentTimeout = orderConfig.getCommentTimeout();
        // 得到超时的时间点
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(commentTimeout);

        // 设置查询条件
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLessThan("createTime", localDateTime);
        criteria.andEqualTo("orderStatus", 0);
        criteria.andEqualTo("isDelete", 0);
        // 查询超时订单
        List<Order> orders = orderMapper.selectByExample(example);
        for (Order order : orders) {
            OrderLog orderLog = new OrderLog();
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderStatus("4");
            orderLog.setPayStatus(order.getPayStatus());
            orderLog.setConsignStatus(order.getConsignStatus());
            orderLog.setRemarks("超时订单，系统自动关闭");
            orderLog.setOrderId(order.getId());
            orderLogMapper.insert(orderLog);

            // 更新订单状态
            order.setOrderStatus("4");
            order.setCloseTime(new Date());// 关闭时间
            orderMapper.updateByPrimaryKeySelective(order);
        }


    }

    @Override
    public OrderResult findOrderResultById(String id) {
        // 创建orderResult对象
        OrderResult orderResult = new OrderResult();
        // 通过id查询订单值
        Order order = orderMapper.selectByPrimaryKey(id);
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", id);
        // 通过id查询订单明细
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(example);
        // 赋值
        orderResult.setOrder(order);
        orderResult.setOrderItemsList(orderItemList);
        return orderResult;
    }

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Order> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Order> orders = (Page<Order>) orderMapper.selectAll();
        return new PageResult<Order>(orders.getTotal(), orders.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        return new PageResult<Order>(orders.getTotal(), orders.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增购物车商品信息
     *
     * @param order
     */
    @Transactional
    public Map<String, Object> add(Order order) {
        // 获取购物车(刷新价格)
        List<Map<String, Object>> cartList = cartService.findNewOrderItemList(order.getUsername());
        // 获取选中的购物车
        List<OrderItem> orderItemList = cartList
                .stream()
                .filter(cart -> (boolean) cart.get("checked"))
                .map(cart -> (OrderItem) cart.get("item"))
                .collect(Collectors.toList());
        // 扣减库存
        if (!skuService.deductionStock(orderItemList)) {
            throw new RuntimeException("库存扣减失败");
        }
        try {
            // 保存订单主表
            order.setId(idWorker.nextId() + "");
            // 合计数计算
            IntStream numStream = orderItemList.stream().mapToInt(OrderItem::getNum);
            IntStream moneyStream = orderItemList.stream().mapToInt(OrderItem::getMoney);
            int totalNum = numStream.sum(); // 总数量
            int totalMoney = moneyStream.sum();// 订单总金额
            int preMoney = cartService.preferential(order.getUsername());// 计算优惠金额
            order.setTotalNum(totalNum);// 总数量
            order.setTotalMoney(totalMoney);// 总金额
            order.setPreMoney(preMoney);//优惠金额
            order.setPayMoney(totalMoney-preMoney);//支付金额=总金额+优惠金额
            order.setCreateTime(new Date());//订单创建日期
            order.setOrderStatus("0"); // 订单状态
            order.setPayStatus("0"); // 支付状态：未支付
            order.setConsignStatus("0");   //发货状态：未发货
            orderMapper.insert(order);
            //打折比例
            double proportion = (double) order.getPayMoney() / totalMoney;
            // 保存订单明细
            for (OrderItem orderItem : orderItemList) {
                orderItem.setOrderId(order.getId());//订单主表ID
                orderItem.setId(idWorker.nextId() + "");
                orderItem.setPayMoney((int) (orderItem.getMoney() * proportion));//支付金额
                orderItemMapper.insert(orderItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 发送回滚消息
            rabbitTemplate.convertAndSend("","queue.skuback",JSON.toJSONString(cartList));
        }
        //清除选中的购物车
        cartService.deleteCheckedCart(order.getUsername());

        // 返回订单号和支付的金额
        Map<String, Object> map = new HashMap<>();
        map.put("ordersn",order.getId());
        map.put("money",order.getPayMoney());
        return map;
    }

    /**
     * 修改
     *
     * @param order
     */
    public void update(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }


    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 订单id
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andLike("id", "%" + searchMap.get("id") + "%");
            }
            // 支付类型，1、在线支付、0 货到付款
            if (searchMap.get("payType") != null && !"".equals(searchMap.get("payType"))) {
                criteria.andLike("payType", "%" + searchMap.get("payType") + "%");
            }
            // 物流名称
            if (searchMap.get("shippingName") != null && !"".equals(searchMap.get("shippingName"))) {
                criteria.andLike("shippingName", "%" + searchMap.get("shippingName") + "%");
            }
            // 物流单号
            if (searchMap.get("shippingCode") != null && !"".equals(searchMap.get("shippingCode"))) {
                criteria.andLike("shippingCode", "%" + searchMap.get("shippingCode") + "%");
            }
            // 用户名称
            if (searchMap.get("username") != null && !"".equals(searchMap.get("username"))) {
                criteria.andLike("username", "%" + searchMap.get("username") + "%");
            }
            // 买家留言
            if (searchMap.get("buyerMessage") != null && !"".equals(searchMap.get("buyerMessage"))) {
                criteria.andLike("buyerMessage", "%" + searchMap.get("buyerMessage") + "%");
            }
            // 是否评价
            if (searchMap.get("buyerRate") != null && !"".equals(searchMap.get("buyerRate"))) {
                criteria.andLike("buyerRate", "%" + searchMap.get("buyerRate") + "%");
            }
            // 收货人
            if (searchMap.get("receiverContact") != null && !"".equals(searchMap.get("receiverContact"))) {
                criteria.andLike("receiverContact", "%" + searchMap.get("receiverContact") + "%");
            }
            // 收货人手机
            if (searchMap.get("receiverMobile") != null && !"".equals(searchMap.get("receiverMobile"))) {
                criteria.andLike("receiverMobile", "%" + searchMap.get("receiverMobile") + "%");
            }
            // 收货人地址
            if (searchMap.get("receiverAddress") != null && !"".equals(searchMap.get("receiverAddress"))) {
                criteria.andLike("receiverAddress", "%" + searchMap.get("receiverAddress") + "%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if (searchMap.get("sourceType") != null && !"".equals(searchMap.get("sourceType"))) {
                criteria.andLike("sourceType", "%" + searchMap.get("sourceType") + "%");
            }
            // 交易流水号
            if (searchMap.get("transactionId") != null && !"".equals(searchMap.get("transactionId"))) {
                criteria.andLike("transactionId", "%" + searchMap.get("transactionId") + "%");
            }
            // 订单状态
            if (searchMap.get("orderStatus") != null && !"".equals(searchMap.get("orderStatus"))) {
                criteria.andLike("orderStatus", "%" + searchMap.get("orderStatus") + "%");
            }
            // 支付状态
            if (searchMap.get("payStatus") != null && !"".equals(searchMap.get("payStatus"))) {
                criteria.andLike("payStatus", "%" + searchMap.get("payStatus") + "%");
            }
            // 发货状态
            if (searchMap.get("consignStatus") != null && !"".equals(searchMap.get("consignStatus"))) {
                criteria.andLike("consignStatus", "%" + searchMap.get("consignStatus") + "%");
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andLike("isDelete", "%" + searchMap.get("isDelete") + "%");
            }

            // 数量合计
            if (searchMap.get("totalNum") != null) {
                criteria.andEqualTo("totalNum", searchMap.get("totalNum"));
            }
            // 金额合计
            if (searchMap.get("totalMoney") != null) {
                criteria.andEqualTo("totalMoney", searchMap.get("totalMoney"));
            }
            // 优惠金额
            if (searchMap.get("preMoney") != null) {
                criteria.andEqualTo("preMoney", searchMap.get("preMoney"));
            }
            // 邮费
            if (searchMap.get("postFee") != null) {
                criteria.andEqualTo("postFee", searchMap.get("postFee"));
            }
            // 实付金额
            if (searchMap.get("payMoney") != null) {
                criteria.andEqualTo("payMoney", searchMap.get("payMoney"));
            }

        }
        return example;
    }

}
