package com.qingcheng.pojo.goods;


import java.io.Serializable;
import java.util.List;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-06-25    19:06
 */

/**
 * 商品组合实体类
 */
public class Goods implements Serializable {
    private Spu spu;
    private List<Sku> skuList;

    @Override
    public String toString() {
        return "Goods{" +
                "spu='" + spu + '\'' +
                ", skuList=" + skuList +
                '}';
    }

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
