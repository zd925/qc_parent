package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.service.system.AdminService;
import com.qingcheng.service.system.ResourceService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-06-30    14:49
 */

public class UserDetailServiceImpl implements UserDetailsService {

    @Reference
    private AdminService adminService;
    @Reference
    private ResourceService resourceService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        // 查询管理员
        Map map = new HashMap<>();
        map.put("loginName", s);
        map.put("status", "1");
        List<Admin> list = adminService.findList(map);
        if (list.size() == 0) {
            return null;
        }
        // 构建角色集合,项目中此处应该是根据用户名查询用户的角色列表
        List<GrantedAuthority> grantedAuths = new ArrayList<>();

        // 获取方法
        List<String> resKeyByLoginName = resourceService.findResKeyByLoginName(s);
        for (String s1 : resKeyByLoginName) {
            grantedAuths.add(new SimpleGrantedAuthority(s1));

        }
        return new User(s, list.get(0).getPassword(), grantedAuths);
    }
}
