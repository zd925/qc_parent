package com.qingcheng.service.order;

import javax.print.DocFlavor;
import java.util.List;
import java.util.Map;

/**
 * 购物车服务
 */
public interface CartService {

    /**
     * 从redis中提取购物车
     * @param username
     * @return
     */
    public List<Map<String,Object>> findCartList(String username);


    /**
     * 添加商品到购物车
     * @param username
     * @param skuId
     * @param num
     */
    public void addItem(String username,String skuId,Integer num);

    /**
     * 更新勾选中状态
     * @param username
     * @param skuId
     * @return
     */
    public boolean updateChecked(String username,String skuId,boolean checked);

    /**
     * 删除选中的购物车
     * @param username
     */
    public void deleteCheckedCart(String username);

    /**
     * 计算当前选中的购物车的优惠金额
     * @param username
     * @return
     */
    public int preferential(String username);

    /**
     *  获取最新的商品价格
     * @param username
     */
    public List<Map<String,Object>> findNewOrderItemList(String username);

}
