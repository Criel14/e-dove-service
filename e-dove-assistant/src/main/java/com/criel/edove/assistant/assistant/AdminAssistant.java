package com.criel.edove.assistant.assistant;

import dev.langchain4j.service.*;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

/**
 * 流式输出大模型
 */
@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        streamingChatModel = "openAiStreamingChatModel",
        chatMemoryProvider = "chatMemoryProvider",
        tools = {
                "adminChatTool"
        }
)
public interface AdminAssistant {

    @SystemMessage(fromResource = "/prompt/system-prompt-admin.md")
    TokenStream adminChat(@MemoryId String memeryId, @UserMessage String message);

}
