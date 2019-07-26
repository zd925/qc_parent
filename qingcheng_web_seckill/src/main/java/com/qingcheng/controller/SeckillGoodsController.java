package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.service.seckill.SeckillGoodsService;
import com.qingcheng.util.DateUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * Created by Super_DH
 * The ideal is to be authentic but larger than life.
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-24    11:25
 */
@RestController
@RequestMapping("/seckill/goods")
public class SeckillGoodsController {


    @Reference
    private SeckillGoodsService seckillGoodsService;
    /**
     * 获取时间菜单
     * @return
     */
    @GetMapping("/menus")
    public List<Date> dateMenus(){
        return DateUtil.getDateMenus();
    }

    /**
     * 对应时间端秒杀商品集合查询
     * 调用service查询数据
     * @param time  2019-5-7 16:00, 可以调用DateUtils,将他转成2019050716格式
     * @return
     */
    @GetMapping("/list")
    public List<SeckillGoods> list(String time){
        // 调用service查询数据

        return seckillGoodsService.list(time);
    }



    /**
     * URl :/seckill/goods/one
     * 根据ID查询商品
     * 调用Service查询商品详情
     * @param time
     * @param id
     * @return
     */
    @GetMapping("/one")
    public SeckillGoods one(String time,Long id){
        // 调用Service查询商品详情
        return seckillGoodsService.one(time, id);
    }
}
