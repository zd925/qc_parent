package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.SkuMapper;
import com.qingcheng.dao.StockBackMapper;
import com.qingcheng.pojo.goods.StockBack;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.StockBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-21    19:54
 */
@Service
public class StockBackServiceImpl implements StockBackService {

    @Autowired
    private StockBackMapper stockBackMapper;
    @Autowired
    private SkuMapper skuMapper;

    @Override
    public void addList(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            StockBack stockBack = new StockBack();
            stockBack.setOrderId(orderItem.getOrderId());
            stockBack.setSkuId(orderItem.getSkuId());
            stockBack.setStatus("0");
            stockBack.setNum(orderItem.getNum());
            stockBack.setCreateTime(new Date());
            stockBackMapper.insert(stockBack);
        }
    }

    @Override
    @Transactional
    public void doBack() {
        System.out.println("库存回滚任务开始");
        StockBack stockBack = new StockBack();
        stockBack.setStatus("0");
        List<StockBack> stockBackList = stockBackMapper.select(stockBack);// 查询数据库状态为0的记录
        for (StockBack stockBack1 : stockBackList) {
            // 添加库存
            skuMapper.deductionStock(stockBack.getSkuId(),-stockBack1.getNum());

            // 减少销量
            skuMapper.addSaleNum(stockBack1.getSkuId(),-stockBack.getNum());
            stockBack1.setStatus("1");
            stockBack1.setBackTime(new Date());
            stockBackMapper.updateByPrimaryKey(stockBack1);
        }
        System.out.println("库存回滚任务结束");
    }
}
