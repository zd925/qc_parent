package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.SeckillGoodsMapper;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.service.seckill.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * Created by Super_DH
 * The ideal is to be authentic but larger than life.
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-24    09:50
 */
@Service
public class SeckillGoodsServiceImp implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;



    /**
     *  获取指定时间对应的秒杀商品列表
     * @param time
     * @return
     */
    @Override
    public List<SeckillGoods> list(String time) {
       return redisTemplate.boundHashOps("SeckillGoods_"+time).values();
    }

    /**
     * 根据id查询商品详情
     * @param time
     * @param id
     * @return
     */
    @Override
    public SeckillGoods one(String time, Long id) {
      return (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_"+time).get(id);
    }
}
