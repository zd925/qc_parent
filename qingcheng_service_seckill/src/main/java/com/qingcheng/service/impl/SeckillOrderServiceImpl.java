package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.SeckillGoodsMapper;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.pojo.seckill.SeckillOrder;
import com.qingcheng.service.seckill.SeckillOrderService;
import com.qingcheng.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

/**
 * Created by Super_DH
 * The ideal is to be authentic but larger than life.
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-25    09:06
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;


    /**
     * 添加订单
     *
     * @param id       商品ID
     * @param time     商品秒杀开始时间
     * @param username 用户登录名
     * @return
     */
    @Override
    public Boolean add(Long id, String time, String username) {
        // 获取商品数据
        SeckillGoods goods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(id);

        // 如果没用库存,则直接抛出异常
        if (goods == null || goods.getStockCount() <= 0) {
            throw new RuntimeException("已售罄!");
        }
        // 如果有库存,则创建秒杀商品订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(id);
        seckillOrder.setMoney(goods.getCostPrice());
        seckillOrder.setUserId(username);
        seckillOrder.setSellerId(goods.getSellerId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("0");
        // 将秒杀订单存入到redis中
        redisTemplate.boundHashOps("SeckillOrder").put(username,seckillOrder);

        // 库存减少
        goods.setStockCount(goods.getStockCount()-1);

        // 判断当前商品是否还有库存
        if (goods.getStockCount()<=0){
            // 并且将商品数据同步到MySql中
            seckillGoodsMapper.updateByPrimaryKeySelective(goods);
            // 弱国没有库存,则清空redis缓存中该商品
            redisTemplate.boundHashOps("SeckillGoods_"+time).delete(id);
        }else {
            // 如果有库存,则直数据重置到redis中
            redisTemplate.boundHashOps("SeckillGoods_"+time).put(id,goods);
        }
        return true;
    }
}
