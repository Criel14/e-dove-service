package com.criel.edove.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.criel.edove.common.context.UserInfoContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 请求拦截器：获取用户信息
 */
@Component
public class UserInfoContextInterceptor implements HandlerInterceptor {

    private final Logger LOGGER = LoggerFactory.getLogger(UserInfoContextInterceptor.class);

    /**
     * 获取请求头中的用户信息并保存到ThreadLocal
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("X-User-Id");
        String userName = request.getHeader("X-User-Name");
        String phoneNumber = request.getHeader("X-Phone-Number");
        if (!StrUtil.isEmpty(userId) && !StrUtil.isEmpty(userName) && !StrUtil.isEmpty(phoneNumber)) {
            UserInfoContextHolder.setUserInfoContext(Long.valueOf(userId), userName, phoneNumber);
            LOGGER.info("当前登录用户：user id: {}, user name: {}", userId, userName);
        }
        return true;
    }

    /**
     * 请求完成后清除ThreadLocal中的数据
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserInfoContextHolder.clearUserInfoContext();
    }
}
