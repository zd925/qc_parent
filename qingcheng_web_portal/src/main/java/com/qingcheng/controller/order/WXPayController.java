package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.order.WXPayService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Super_DH
 * The ideal is to be authentic but larger than life.
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-22    20:51
 */
@RestController
@RequestMapping("/wxpay")
public class WXPayController {
    @Reference
    private OrderService orderService;
    @Reference
    private WXPayService wxPayService;

    @GetMapping("/createNative")
    public Map createNative(String orderId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Order order = orderService.findById(orderId);
        if (order!=null){
            if ("0".equals(order.getPayStatus()) && "0".equals(order.getOrderStatus()) &&  username.equals(order.getUsername())){
                System.out.println("生成notifyUrl");

                return wxPayService.createNative(orderId,order.getPayMoney(),"dh925.wicp.vip:39726/wxpay/notify.do");
            }else{
                return null;
            }
        }else {
            return null;
        }
    }


    @RequestMapping("/notify")
    public Map notifyLogin(HttpServletRequest request){
        // 回调的是二进制流,应该转换为字符串
        System.out.println("回调了");
        try {
            ServletInputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len=0;
            while ((len=inStream.read(buffer))!=-1){
                outSteam.write(buffer,0,len);
            }
            outSteam.close();
            inStream.close();
            // 转化为字符串
            String result = new String(outSteam.toByteArray(), "utf-8");
            System.out.println(result);
            wxPayService.notifyLogic(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap();


    }
}
