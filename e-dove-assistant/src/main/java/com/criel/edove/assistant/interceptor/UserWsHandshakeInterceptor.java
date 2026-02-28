package com.criel.edove.assistant.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket拦截器，从请求头里获取用户数据，并存入会话属性中（握手阶段）
 */
@Component
public class UserWsHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        // 从请求头获取用户信息
        HttpHeaders headers = request.getHeaders();
        String userId = headers.getFirst("X-User-Id");
        String username = headers.getFirst("X-Username");
        String phone = headers.getFirst("X-Phone");

        if (userId == null || username == null || phone == null) {
            return false;
        }

        // 将用户信息放入 WebSocket 的会话属性 attributes 中
        attributes.put("userId", Long.parseLong(userId));
        attributes.put("username", username);
        attributes.put("phone", phone);
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
    }
}
