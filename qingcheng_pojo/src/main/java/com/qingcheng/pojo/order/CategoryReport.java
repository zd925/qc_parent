package com.qingcheng.pojo.order;

import sun.rmi.server.InactiveGroupException;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-06-28    14:33
 */
@Table(name = "tb_category_report")
public class CategoryReport implements Serializable {

    @Id
    private Integer categoryId1;// 一级分类
    @Id
    private Integer categoryId2;// 二级分类
    @Id
    private Integer categoryId3;// 三级分类
    private Date countDate;// 统计日期
    private Integer num;// 销售量
    private Integer money;// 销售额

    @Override
    public String toString() {
        return "CategoryReport{" +
                "categoryId1=" + categoryId1 +
                ", categoryId3=" + categoryId3 +
                ", categoryId2=" + categoryId2 +
                ", countDate=" + countDate +
                ", num=" + num +
                ", money=" + money +
                '}';
    }

    public Integer getCategoryId1() {
        return categoryId1;
    }

    public void setCategoryId1(Integer categoryId1) {
        this.categoryId1 = categoryId1;
    }

    public Integer getCategoryId3() {
        return categoryId3;
    }

    public void setCategoryId3(Integer categoryId3) {
        this.categoryId3 = categoryId3;
    }

    public Integer getCategoryId2() {
        return categoryId2;
    }

    public void setCategoryId2(Integer categoryId2) {
        this.categoryId2 = categoryId2;
    }

    public Date getCountDate() {
        return countDate;
    }

    public void setCountDate(Date countDate) {
        this.countDate = countDate;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }
}
