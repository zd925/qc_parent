package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.StockBackService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-21    20:11
 */
@Component
public class SkuTask {
    @Reference
    private StockBackService stockBackService;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void orderTimeOutLogic(){
        System.out.println("执行库存回滚");
        stockBackService.doBack();
    }
}
