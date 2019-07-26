package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.LoginLog;
import com.qingcheng.service.system.LoginLogService;
import com.qingcheng.utils.WebUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-06-30    15:35
 */
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Reference
    private LoginLogService loginLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

        // 记录日志
        String loginName = authentication.getName(); // 记录用户名
        String ip = httpServletRequest.getRemoteAddr();// 记录访问ip
        // 创建一个类,储存数据
        LoginLog loginLog = new LoginLog();
        loginLog.setLoginName(loginName);
        loginLog.setIp(ip);
        loginLog.setLoginTime(new Date());
        // 通过webuitl工具类获取城市地址,底层使用ip查询百度直接获取城市地址
        loginLog.setLocation(WebUtil.getCityByIP(ip));
        // 通过请求获取浏览器名字
        // 使用工具类判断请求头中是否包含浏览器名字.多个if语句判断
        String header = httpServletRequest.getHeader("user-agent");
        loginLog.setBrowserName(WebUtil.getBrowserName(header));

        loginLogService.add(loginLog);


        // 日志记录成功后,转发到main.html,
        httpServletRequest.getRequestDispatcher("/main.html").forward(httpServletRequest, httpServletResponse);

    }
}
