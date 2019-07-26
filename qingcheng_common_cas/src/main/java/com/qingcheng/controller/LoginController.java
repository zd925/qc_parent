package com.qingcheng.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 获取用户名
     * @return
     */
    @RequestMapping("/username")
    public Map username(){
        // 等到登录的账号
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 判断登录者
        System.out.println("当前登录用户："+username);
        if("anonymousUser".equals(username)){ //未登录
            username="";
        }
        // 存入map返回
        Map map = new HashMap();
        map.put("username",username);
        return map;
    }
}
