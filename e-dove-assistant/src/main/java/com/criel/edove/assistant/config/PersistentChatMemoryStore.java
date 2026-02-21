package com.criel.edove.assistant.config;

import com.criel.edove.common.constant.RedisKeyConstant;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PersistentChatMemoryStore implements ChatMemoryStore {

    private final RedissonClient redissonClient;

    // 存2天，第二天可以继续对话，隔一天没对话即删除
    private final Duration ttl = Duration.ofDays(2);

    /**
     * 获取会话消息
     */
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        RBucket<String> rBucket = getRBucket(memoryId);
        if (rBucket.isExists()) {
            rBucket.expire(ttl); // 延期
            return ChatMessageDeserializer.messagesFromJson(rBucket.get());
        } else {
            return List.of();
        }
    }

    /**
     * 更新会话消息
     */
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        RBucket<String> rBucket = getRBucket(memoryId);
        String messagesJson = ChatMessageSerializer.messagesToJson(messages);
        rBucket.set(messagesJson, ttl);
    }

    /**
     * 删除会话消息
     */
    @Override
    public void deleteMessages(Object memoryId) {
        RBucket<String> rBucket = getRBucket(memoryId);
        rBucket.delete();
    }

    /**
     * 获取Redis键，同时验证memoryId的类型为String
     */
    private RBucket<String> getRBucket(Object memoryId) {
        if (memoryId == null) {
            throw new IllegalArgumentException("memoryId 不能为空");
        }
        if (!(memoryId instanceof String str)) {
            throw new IllegalArgumentException("memoryId 必须是 String");
        }
        return redissonClient.getBucket(RedisKeyConstant.AI_CHAT_MEMORY + str);
    }

}
