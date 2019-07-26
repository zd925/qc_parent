package com.qingcheng.controller.order;

import com.qingcheng.service.order.CategoryReportService;
import com.qingcheng.service.order.OrderService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-06-27    19:58
 */
@Component
public class OrderTask {

    @Reference
    private OrderService orderService;
    @Reference
    private CategoryReportService categoryReportService;
    @Scheduled(cron = "0 0 1 * * ?")
    public void createCategoryReportService(){
        categoryReportService.createData();
    }

    @Scheduled(cron = "2 * * * * ?")
    public void TimeOutLogic() {
       /* System.out.println("我执行了丫" + new Date());*/
        orderService.orderTimeOutLogic();
    }

}
