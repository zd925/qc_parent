package com.qingcheng.service.seckill;

import com.qingcheng.pojo.seckill.SeckillGoods;

import java.util.List;

/**
 * Created by Super_DH
 * The ideal is to be authentic but larger than life.
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-24    09:51
 */
public interface SeckillGoodsService {

    /**
     *  获取指定时间对应的秒杀商品列表
     * @param key
     * @return
     */
    public List<SeckillGoods> list(String key);

    /**
     * 根据id查询商品详情
     * @param time
     * @param id
     * @return
     */
    public SeckillGoods one(String time,Long id);

}
