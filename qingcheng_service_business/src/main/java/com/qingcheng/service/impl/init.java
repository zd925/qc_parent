package com.qingcheng.service.impl;

import com.qingcheng.service.business.AdService;
import com.qingcheng.service.goods.CategoryService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-07-05    16:06
 */

@Component
public class init implements InitializingBean {

    @Autowired
    private AdService adService;
    /**
     * 项目在加载时候,加载方法下的方法 功能是缓存预热
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("缓存预热");
       adService.saveAllToRedis(); // 广告存入缓存
    }
}
