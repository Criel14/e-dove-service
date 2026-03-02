package com.criel.edove.assistant.handler;

import cn.hutool.core.util.StrUtil;
import com.criel.edove.assistant.dto.WsChatRequestDTO;
import com.criel.edove.assistant.service.AssistantService;
import com.criel.edove.assistant.vo.WsChatResponseVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 小程序用户端聊天主要逻辑
 */
@Component
@RequiredArgsConstructor
public class UserChatWebSocketHandler extends TextWebSocketHandler {

    private final AssistantService assistantService;
    private final ObjectMapper objectMapper;

    /**
     * 收到消息时触发
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WsChatRequestDTO request;
        try {
            // 解析前端发送的 JSON 消息到 DTO
            request = objectMapper.readValue(message.getPayload(), WsChatRequestDTO.class);
        } catch (Exception e) {
            // JSON 不合法或字段结构不匹配时，直接返回协议错误并结束本次处理
            send(
                    session,
                    WsChatResponseVO.builder()
                            .type("error")
                            .error("消息格式错误，请发送 JSON 格式的消息")
                            .build()
            );
            return;
        }

        // 读取消息类型
        String type = request.getType();
        // 心跳
        if ("ping".equals(type)) {
            // 小程序可定时发送 ping 保活；服务端返回 pong
            send(
                    session,
                    WsChatResponseVO.builder()
                            .type("pong")
                            .build()
            );
            return;
        }
        // 聊天信息：仅支持 chat 类型，其它类型一律拒绝
        if (!"chat".equals(type)) {
            send(
                    session,
                    WsChatResponseVO.builder()
                            .type("error")
                            .error("不支持的消息类型")
                            .build()
            );
            return;
        }

        // 从 attributes 中获取 userId，并校验用户身份
        Long userId = (Long) session.getAttributes()
                .get("userId");
        if (userId == null) {
            // 用户身份缺失，说明未登录/令牌失效/握手未通过
            send(
                    session,
                    WsChatResponseVO.builder()
                            .type("error")
                            .error("未登录或登录已失效")
                            .build()
            );
            // 使用策略违规状态关闭连接，避免后续继续发送无效请求
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        // 业务参数校验：会话ID和消息内容都必须存在
        if (StrUtil.hasBlank(request.getMemoryId(), request.getMessage())) {
            send(
                    session,
                    WsChatResponseVO.builder()
                            .type("error")
                            .memoryId(request.getMemoryId())
                            .error("memoryId 和 message 不能为空")
                            .build()
            );
            return;
        }

        // 先返回一个 ack：用于告诉前端请求已受理，马上开始流式返回
        send(
                session, WsChatResponseVO.builder()
                        .type("ack")
                        .memoryId(request.getMemoryId())
                        .build()
        );

        // 启动流式聊天，并通过回调把 token/done/error 推送给前端
        AtomicLong seq = new AtomicLong(0); // token 序号，便于前端按序拼接/排查问题
        assistantService.userChatStream(
                userId,
                request.getMemoryId(),
                request.getMessage(),
                // 每收到一个分片 token 就向前端推送一次
                token -> safeSend(
                        session,
                        WsChatResponseVO.builder()
                                .type("token")
                                .memoryId(request.getMemoryId())
                                .seq(seq.incrementAndGet())
                                .content(token)
                                .build()
                ),
                // 模型完整输出结束
                () -> safeSend(
                        session,
                        WsChatResponseVO.builder()
                                .type("done")
                                .memoryId(request.getMemoryId())
                                .build()
                ),
                // 流式过程出错（网络/模型/下游异常等）
                err -> safeSend(
                        session,
                        WsChatResponseVO.builder()
                                .type("error")
                                .memoryId(request.getMemoryId())
                                .error(err.getMessage())
                                .build()
                )
        );
    }

    /**
     * 安全发送包装：
     * 在异步回调中调用 send 时，若连接已关闭或序列化失败，不再向上抛异常，
     * 防止回调线程被异常中断，影响后续流程收敛。
     */
    private void safeSend(WebSocketSession session, WsChatResponseVO response) {
        try {
            send(session, response);
        } catch (Exception ignored) {
        }
    }

    /**
     * 统一发送 JSON 文本消息。
     * 同一个 WebSocketSession 可能被多个回调线程同时写入（token/done/error），需要加锁
     */
    private void send(WebSocketSession session, WsChatResponseVO response) throws IOException {
        // 使用 session 对象加锁，避免并发写导致帧交错或状态异常
        synchronized (session) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            }
        }
    }

}
