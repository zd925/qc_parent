package com.qingcheng.pojo.goods;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-21    19:50
 */
@Table(name = "tb_stock_back")
public class StockBack {
    @Id
    private String orderId;

    @Id
    private String skuId;

    private Integer num;

    private String status;

    private Date createTime;

    private Date backTime;

    @Override
    public String toString() {
        return "StockBack{" +
                "orderId='" + orderId + '\'' +
                ", skuId='" + skuId + '\'' +
                ", num=" + num +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", backTime=" + backTime +
                '}';
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getBackTime() {
        return backTime;
    }

    public void setBackTime(Date backTime) {
        this.backTime = backTime;
    }
}
