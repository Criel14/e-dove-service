package com.criel.edove.assistant.service;

import com.criel.edove.assistant.dto.AddressGenerateDTO;
import com.criel.edove.assistant.vo.AddressGenerateVO;
import com.criel.edove.assistant.vo.ChatCreateVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.function.Consumer;

/**
 * 大模型调用服务
 *
 * @author Criel
 * @since 2026-01-15
 */
public interface AssistantService {

    AddressGenerateVO generateAddresses(AddressGenerateDTO addressGenerateDTO);

    SseEmitter adminChat(String memoryId, String message);

    ChatCreateVO createChat();

    void userChatStream(Long userId, String memoryId, String message,
                        Consumer<String> onToken, Runnable onDone, Consumer<Throwable> onError);
}

