package com.criel.edove.assistant.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
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
                "userChatTool"
        }
)
public interface UserAssistant {

    @SystemMessage(fromResource = "/prompt/system-prompt-user.md")
    TokenStream userChat(@MemoryId String memoryId, @UserMessage String message);

}
