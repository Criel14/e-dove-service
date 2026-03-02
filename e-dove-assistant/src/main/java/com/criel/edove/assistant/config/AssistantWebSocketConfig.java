package com.criel.edove.assistant.config;

import com.criel.edove.assistant.handler.UserChatWebSocketHandler;
import com.criel.edove.assistant.interceptor.UserWsHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置：注册小程序用户端 AI 聊天入口
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class AssistantWebSocketConfig implements WebSocketConfigurer {

    private final UserChatWebSocketHandler userChatWebSocketHandler;
    private final UserWsHandshakeInterceptor userWsHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String path = "/assistant/ws/user/chat";
        registry.addHandler(userChatWebSocketHandler, path)
                .addInterceptors(userWsHandshakeInterceptor) // 握手阶段校验并提取用户身份
                .setAllowedOriginPatterns("*");
    }

}
