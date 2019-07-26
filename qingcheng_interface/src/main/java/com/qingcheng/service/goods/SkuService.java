package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.order.OrderItem;

import java.util.*;

/**
 * sku业务逻辑层
 */
public interface SkuService {


    public List<Sku> findAll();


    public PageResult<Sku> findPage(int page, int size);


    public List<Sku> findList(Map<String,Object> searchMap);


    public PageResult<Sku> findPage(Map<String,Object> searchMap,int page, int size);


    public Sku findById(String id);

    public void add(Sku sku);


    public void update(Sku sku);


    public void delete(String id);

    /**
     * 保存所有价格到缓存
     */
    public void saveAllPriceToRedis();

    /**
     * 根据id 查询商品的价格
     */
    public  Integer findPriceToRedis(String id);

    /**
     *  保存价格到缓存
     * @param
     */
    public void savePriceToRedisById(String id,Integer price);

    /**
     * 根据sku id 删除商品价格缓存
     * @param id
     */
    public void deletePriceFromRedis(String id);

    /**
     *  根据购物车批量扣减库存
     * @param orderItemList
     * @return
     */
    public boolean deductionStock(List<OrderItem> orderItemList);
}
