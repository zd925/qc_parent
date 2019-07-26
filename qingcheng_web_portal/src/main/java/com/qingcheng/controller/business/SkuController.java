package com.qingcheng.controller.business;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.SkuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-07-05    21:55
 */
@RestController
@RequestMapping("/sku")
@CrossOrigin
public class SkuController {
    @Reference
    private SkuService skuService;
    @GetMapping("/price")
    public Integer price(String id ){
        return (Integer) skuService.findPriceToRedis(id);
    }
}
