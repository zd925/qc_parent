package com.qingcheng.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.service.seckill.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Super_DH
 * The ideal is to be authentic but larger than life.
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-25    09:03
 */
@RestController
@RequestMapping("/seckill/order")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    /*****
     * url:/seckill/order/add.do
     * @param id
     * @param time
     * @return
     */
    @RequestMapping(value = "/add")
    public Result add(Long id,String time){
        try {
            // 获取用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            // 如果用户账号为anonymousUser,则表明用户为登录
            if (username.equalsIgnoreCase("anonymousUser")){
                // 这里403错误代码表示用户没有登录
                return new Result(403,"请先登录!");
            }
            Boolean flag = seckillOrderService.add(id, time, username);
            if (flag){
                return new Result(0,"下单成功!");
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
        return new Result(1,"秒杀下单失败!!!!");
    }


}
