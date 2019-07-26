package com.qingcheng.dao;

import com.qingcheng.pojo.order.CategoryReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-06-28    16:01
 */

public interface CategoryReportMapper<list> extends Mapper<CategoryReport> {

    @Select("SELECT  " +
            " oi.category_id1 categoryId1,  " +
            " oi.category_id2 categoryId2,  " +
            " oi.category_id3 categoryId3,  " +
            " DATE_FORMAT( o.pay_time, '%y_%m_%d' ) countDate,  " +
            " SUM( oi.num ) num,  " +
            " SUM( oi.pay_money ) money   " +
            "FROM  " +
            " tb_order o,  " +
            " tb_order_item oi   " +
            "WHERE  " +
            " o.id = oi.order_id   " +
            " AND o.is_delete = '0'   " +
            " AND DATE_FORMAT( o.pay_time, '%y-%m-%d' ) = '19-06-27'   " +
            "GROUP BY  " +
            " oi.category_id1,  " +
            " oi.category_id2,  " +
            " oi.category_id3,  " +
            " DATE_FORMAT( o.pay_time, '%Y-%m-%d' )")
    public List<CategoryReport> categoryReport(@Param("date")LocalDate date);

    @Select("SELECT " +
            "category_id1 categoryId1, " +
            "c.name categoryName, " +
            "DATE_FORMAT( o.pay_time, '%Y_%m_%d' ) countDate, " +
            "SUM( oi.num ) num, " +
            "SUM( oi.pay_money ) money  " +
            "FROM " +
            "tb_category_report r,v_category c  " +
            "WHERE " +
            "r.category_id1=c.id " +
            "count_date >= #{date1}  " +
            "AND count_date <= #{date2}  " +
            "GROUP BY " +
            "category_id1,c.name")
    List<Map> category1Count(@Param("date1") String date1, @Param("date2") String date2);

}
