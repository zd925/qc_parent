package com.qingcheng.task;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by Super_DH
 * The ideal is to be authentic but larger than life.
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-26    11:58
 */
@Component
public class MultiThreadingCreateOrder {

    /**
     * 下单操作
     */
    @Async
    public void createOrder() {

        try {
            System.out.println("准备执行");
            Thread.sleep(2000);
            System.out.println("开始执行");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}