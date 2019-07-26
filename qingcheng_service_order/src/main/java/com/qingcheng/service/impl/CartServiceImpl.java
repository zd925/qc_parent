package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.pojo.goods.Category;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.order.PreferentialService;
import com.qingcheng.util.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private SkuService skuService;

    @Reference
    private CategoryService categoryService;

    @Autowired
    private PreferentialService preferentialService;


    /**
     * 从redis中提取购物车
     *
     * @param username
     * @return
     */
    @Override
    public List<Map<String, Object>> findCartList(String username) {
        System.out.println("redis中获取购物车信息" + username);
        List<Map<String, Object>> cartList = (List<Map<String, Object>>) redisTemplate.boundHashOps(CacheKey.CART_LIST).get(username);

        // 如果用户购物车无商品cartList为null
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 添加商品到购物车
     *
     * @param username
     * @param skuId
     * @param num
     */
    @Override
    public void addItem(String username, String skuId, Integer num) {

        // 先遍历购物中是否存在这个商品调用findCartList
        List<Map<String, Object>> cartList = findCartList(username);

        // 判断购物车有无这个商品
        boolean flag = false;

        // 循环cartList查询是否存在这个商品
        for (Map map : cartList) {
            OrderItem orderItem = (OrderItem) map.get("item");
            // 判断orderItem是否存在skuId
            if (orderItem.getSkuId().equals(skuId)) {
                // 存在id
                if (orderItem.getNum() <= 0) {
                    cartList.remove(map);
                    flag = true;
                    break;
                }

                // 数量 重量 价格
                int weight = orderItem.getWeight() / orderItem.getNum();//商品单个重量
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setMoney(orderItem.getPrice() * orderItem.getNum());
                orderItem.setWeight(weight * orderItem.getNum());
                if (orderItem.getNum() <= 0) {
                    cartList.remove(map);
                }

                flag = true;
                break;
            }
        }
        // 如果购物车不存在这个商品,添加商品
        if (flag == false) {

            Sku sku = skuService.findById(skuId);
            if (sku == null) {
                throw new RuntimeException("商品不存在");
            }
            if (!"1".equals(sku.getStatus())) {
                throw new RuntimeException("商品状态不符合法");
            }
            if (num <= 0) {
                throw new RuntimeException("商品库存不足");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(skuId);
            orderItem.setSpuId(sku.getSpuId());
            orderItem.setNum(num);
            orderItem.setImage(sku.getImage());
            orderItem.setPrice(sku.getPrice());
            orderItem.setName(sku.getName());
            orderItem.setMoney(orderItem.getPrice() * num);//金额计算
            if (sku.getWeight() == null) {
                sku.setWeight(0);
            }
            orderItem.setWeight(sku.getWeight() * num);//重量计算

            // 商品分类
            orderItem.setCategoryId3(sku.getCategoryId());

            Category category3 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(sku.getCategoryId());
            if (category3 == null) {
                category3 = categoryService.findById(sku.getCategoryId());//根据3级分类id查2级
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(sku.getCategoryId(), category3);// 再存入缓存
            }
            orderItem.setCategoryId2(category3.getParentId());

            Category category2 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(category3.getParentId());
            if (category2 == null) {
                category2 = categoryService.findById(category3.getParentId());//根据2级分类id查询1级
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(category3.getParentId(), category2);// 再存入缓存
            }
            orderItem.setCategoryId1(category2.getParentId());


            Map map = new HashMap();
            map.put("item", orderItem);
            map.put("checked", true);
            cartList.add(map);
        }

        // 保存缓存
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, cartList);


    }


    /**
     * 保存勾选中状态
     *
     * @param username
     * @param skuId
     * @return
     */
    @Override
    public boolean updateChecked(String username, String skuId, boolean checked) {
        // 获取购物车的所有商品
        List<Map<String, Object>> cartList = findCartList(username);
        // 判断缓存中是否含有勾选的商品
        boolean isOk = false;

        for (Map map : cartList) {
            OrderItem orderItem = (OrderItem) map.get("item");
            if (orderItem.getSkuId().equals(skuId)) {
                // 更新map中check的值
                map.put("checked", checked);
                isOk = true;
                break;
            }
        }
        if (isOk) {
            redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, cartList);
        }
        return isOk;
    }

    /**
     * 删除选中的购物车
     *
     * @param username
     */
    @Override
    public void deleteCheckedCart(String username) {
        List<Map<String, Object>> cartList = findCartList(username).stream().filter(cart -> (boolean) cart.get("checked") == false).collect(Collectors.toList());
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, cartList);
    }


    /**
     * 计算当前选中的购物车的优惠金额
     *
     * @param username
     * @return
     */
    @Override
    public int preferential(String username) {
        // 获取选中的购物车 List<OrderItem> List<Map>
        List<OrderItem> orderItemList = findCartList(username)
                .stream()
                .filter(cart -> (boolean) cart.get("checked") == true)
                .map(cart -> (OrderItem) cart.get("item"))
                .collect(Collectors.toList());
        // 方法一
         /*  List<Map<String, Object>> cartList = findCartList(username);
        ArrayList<OrderItem> orderItemList = new ArrayList<>();
        for (Map<String, Object> map : cartList) {
            if ((boolean)map.get("checked")==true){
                orderItemList.add((OrderItem) map.get("item"));
            }
        }*/
        // 按分类聚合统计每个分类的金额 group by
        Map<Integer, IntSummaryStatistics> cartMap = orderItemList
                .stream()
                .collect(Collectors.groupingBy(OrderItem::getCategoryId3,
                        Collectors.summarizingInt(OrderItem::getMoney)));
        // 循环结果,统计每个分类的优惠金额,并累加
        int allPreMoney = 0;// 累计优惠金额
        for (Integer categoryId : cartMap.keySet()) {
            // 获取品类的消费总金额
            int money = (int) cartMap.get(categoryId).getSum();
            // 获取优惠金额
            int preMoney = preferentialService.findPreMoneyByCategoryId(categoryId, money);
            System.out.println("分类:" + categoryId + "消费金额:" + money + "优惠金额:" + preMoney);
            allPreMoney += preMoney;
        }


        return allPreMoney;
    }

    /**
     *  刷新当前购物车的商品价格
     * @param username
     * @return
     */
    @Override
    public List<Map<String, Object>> findNewOrderItemList(String username) {
       // 从redis中获取购物车的商品
        List<Map<String, Object>>  cartList = findCartList(username);
        for (Map map:cartList){
            // 循环购物车列表,获取最新的价格表,在保存进去,重新读取每个商品的最新价格
            OrderItem orderItem = (OrderItem) map.get("item");

            Sku sku = skuService.findById(orderItem.getSkuId());

            // 更新价格商品
            orderItem.setPrice(sku.getPrice());
            // 更新价格
            orderItem.setMoney(sku.getPrice()*orderItem.getNum());

        }
        // 保存新的价格存入redis
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username,cartList);
        return cartList;
    }
}
