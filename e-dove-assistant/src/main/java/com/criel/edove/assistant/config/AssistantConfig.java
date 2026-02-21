package com.criel.edove.assistant.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AssistantConfig {

    private final PersistentChatMemoryStore persistentChatMemoryStore;

    /**
     * 创建一个 ChatMemoryProvider
     */
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return new ChatMemoryProvider() {

            /**
             * 创建一个 ChatMemory
             * @param memoryId 用于标记聊天的对象
             */
            @Override
            public ChatMemory get(Object memoryId) {
                // 这里返回的 MessageWindowChatMemory 是基于【消息窗口】记忆和淘汰数据的
                return MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(10) // 这里设置最多10条消息
                        .chatMemoryStore(persistentChatMemoryStore)
                        .build();
            }
        };
    }

}
