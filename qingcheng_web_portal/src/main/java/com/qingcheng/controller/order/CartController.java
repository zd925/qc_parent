package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.user.Address;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.user.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-18    16:41
 */

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Reference
    private AddressService addressService;

    @Reference
    private OrderService orderService;

    /**
     * 获取当前用户名
     */
    private String getUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * 从redis中提取购物车
     */
    @GetMapping("/findCartList")
    public List<Map<String, Object>> findCartList() {

        // 获取登录用户名
        String username = getUsername();

        List<Map<String, Object>> cartList = cartService.findCartList(username);
        System.out.println(cartList);
        return cartList;
    }

    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     */
    @GetMapping("/addItem")
    public Result addItem(String skuId, Integer num) {
        // 获取登录用户名
        String username = getUsername();
        cartService.addItem(username, skuId, num);
        return new Result();
    }

    /**
     * 添加购买商品在跳转转购物车
     * @param response
     * @param skuId
     * @param num
     */
    @GetMapping("/buy")
    public void buy(HttpServletResponse response,String skuId,Integer num) throws IOException {
        // 获取登录用户名
        String username = getUsername();
        cartService.addItem(username, skuId, num);
        response.sendRedirect("/cart.html");


    }


    /**
     * 保存勾选中状态
     * @param skuId
     * @return
     */
    @GetMapping("/updateChecked")
    public Result updateChecked( String skuId,boolean checked) {
        String username = getUsername();
        cartService.updateChecked(username,skuId,checked);
        return new Result();
    }

    /**
     * 删除选中的购物车
     */
    @GetMapping("/deleteCheckedCart")
    public void deleteCheckedCart() {
        String username = getUsername();
        cartService.deleteCheckedCart(username);
    }

    /**
     *  计算当前选中的购物车的优惠金额
     * @return
     */
    @GetMapping("/preferential")
    public Map preferential(){
        String username = getUsername();
        int preferential = cartService.preferential(username);
        Map map =new HashMap();
        map.put("preferential",preferential);
        return map;
    }

    /**
     *  获取刷新单价后的购物车列表
     * @return
     */
    @GetMapping("/findNewOrderItemList")
    public List<Map<String, Object>> findNewOrderItemList(){
        String username = getUsername();
        List<Map<String, Object>> newOrderItemList = cartService.findNewOrderItemList(username);
        return newOrderItemList;

    }

    /**
     * 根据用户名查找地址列表
     * @return 地址列表
     */
    @GetMapping("/findAddressList")
    public List<Address> findAddressList(){
        String username = getUsername();
       return addressService.findByUsername(username);
    }

    /**
     *  保存订单
     * @param order
     * @return
     */
    @PostMapping("/saveOrder")
    public Map<String,Object> saveOrder(@RequestBody Order order){
        String username = getUsername();
        order.setUsername(username);
        return orderService.add(order);
    }
}
