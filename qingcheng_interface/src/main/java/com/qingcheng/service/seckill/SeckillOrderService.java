package com.qingcheng.service.seckill;

/**
 * Created by Super_DH
 * The ideal is to be authentic but larger than life.
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-25    09:06
 */

public interface SeckillOrderService {

    /**
     *  添加秒杀订单
     * @param id 商品ID
     * @param time 商品秒杀开始时间
     * @param username 用户登录名
     * @return 判断是否添加秒杀订单是否成功
     */
    Boolean add(Long id,String time,String username);
}
