package com.qingcheng.controller.business;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.EsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-07-10    10:20
 */
@Controller
@RequestMapping("/search")
public class EsController {
    @Reference
    private EsService esService;
    @RequestMapping("/findAll")
    public void findAll() throws IOException {
        esService.importToEs();
    }
}
