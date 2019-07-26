package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Spu;

import java.util.*;

/**
 * spu业务逻辑层
 */
public interface SpuService {


    public List<Spu> findAll();


    public PageResult<Spu> findPage(int page, int size);


    public List<Spu> findList(Map<String,Object> searchMap);


    public PageResult<Spu> findPage(Map<String,Object> searchMap,int page, int size);


    public Spu findById(String id);

    public void add(Spu spu);


    public void update(Spu spu);


    public void delete(String id);

    /**
     * 保存商品
     * @param goods 商品组合实体类
     */
    void saveGoods(Goods goods);

    /**
     * 根据id查询商品
     * @param id
     * @return
     */
    public Goods findGoodsById(String id);

    /**
     *
     * @param id
     * @param status
     * @param message
     */
    void audit(String id,String status,String message);

    /**
     * 上架功能
     * @param id
     */
    void put(String id);

    /**
     * 下架功能
     * @param id
     */
    void pull(String id);

    /**
     * 上架多个
     * @param ids
     */
    int putMany(String[] ids);



}
