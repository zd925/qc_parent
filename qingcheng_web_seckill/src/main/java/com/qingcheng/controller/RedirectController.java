package com.qingcheng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Super_DH
 * The ideal is to be authentic but larger than life.
 *
 * @version java.11
 * @package qingcheng_parent
 * @date: 2019-07-24    23:12
 */
@Controller
@RequestMapping(value = "/redirect")
public class RedirectController {

    /****
     * URL:/redirect/back
     * 跳转方法
     * referer:用户访问该方法的来源页面地址
     */
    @RequestMapping(value = "/back")
    public String back(@RequestHeader(value = "referer",required = false)String referer){
        if(!StringUtils.isEmpty(referer)){
            return "redirect:"+referer;
        }
        return "/seckill-index.html";
    }
}
