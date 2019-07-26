package com.qingcheng.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.user.User;
import com.qingcheng.service.user.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @GetMapping("/sendSms")
    public Result sendSms(String phone){
        userService.sendSms(phone);
        return new Result();
    }

    @PostMapping("/save")
    public Result add(@RequestBody User user, String smsCode){

       // 密码加密功能
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        String newPassword = bCrypt.encode(user.getPassword());
        user.setPassword(newPassword);
        userService.add(user,smsCode);
        return new Result();

    }

}
