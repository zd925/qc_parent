package com.qingcheng.service.order;

import com.qingcheng.pojo.order.CategoryReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-06-28    16:00
 */
public interface CategoryReportService {

    List<CategoryReport> categoryReport(LocalDate date);

    /**
     * 更新创建时间
     */
    void createData();

    /**
     * 一级类目统计
     * @param date1
     * @param date2
     * @return
     */
    List<Map> category1Count(String date1,String date2);

}
