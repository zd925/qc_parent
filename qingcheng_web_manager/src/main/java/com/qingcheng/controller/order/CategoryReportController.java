package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-06-28    16:07
 */
@RestController
@RequestMapping("/categoryReport")
public class CategoryReportController {

    @Reference
    private CategoryReportService categoryReportService;

    /**
     * 昨天访问数据统计
     */
    @RequestMapping("/yesterday")
    public List<CategoryReport> categoryReport(){
        LocalDate localDate= LocalDate.now().minusDays(1);// 得到昨天的日期
        System.out.println(localDate);
     return categoryReportService.categoryReport(localDate);

    }

    @GetMapping("/category1Count")
    public List<Map> catergory1Count (String date1,String date2){
        return categoryReportService.category1Count(date1,date2);
    }
}
