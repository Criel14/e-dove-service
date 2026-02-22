package com.criel.edove.assistant.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.criel.edove.assistant.assistant.Assistant;
import com.criel.edove.assistant.assistant.AdminAssistant;
import com.criel.edove.assistant.dto.AddressGenerateDTO;
import com.criel.edove.assistant.service.AssistantService;
import com.criel.edove.assistant.vo.AddressGenerateVO;
import com.criel.edove.assistant.vo.ChatCreateVO;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.context.UserInfoContextHolder;
import com.criel.edove.common.service.SnowflakeService;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * 大模型调用服务
 *
 * @author Criel
 * @since 2026-01-15
 */
@Service
@RequiredArgsConstructor
public class AssistantServiceImpl implements AssistantService {

    private final Assistant assistant;
    private final AdminAssistant adminAssistant;
    private final SnowflakeService snowflakeService;
    private final RedissonClient redissonClient;

    private static final MediaType UTF8_TEXT = new MediaType("text", "plain", StandardCharsets.UTF_8);

    /**
     * 生成指定数量的 门店所在区的 随机地址
     *
     * @param addressGenerateDTO 门店的省市区地址 + 要生成的地址数量
     */
    @Override
    public AddressGenerateVO generateAddresses(AddressGenerateDTO addressGenerateDTO) {
        // 调用大模型生成地址数组
        String result = assistant.generateAddress(
                addressGenerateDTO.getCount(),
                addressGenerateDTO.getStoreAddrProvince(),
                addressGenerateDTO.getStoreAddrCity(),
                addressGenerateDTO.getStoreAddrDistrict()
        );
        JSONArray jsonArray = JSONUtil.parseArray(result);
        List<String> addresses = jsonArray.toList(String.class);
        return new AddressGenerateVO(addresses);
    }

    /**
     * 管理端AI对话
     * @return SSE连接对象，服务端不断推新的token给前端
     */
    @Override
    public SseEmitter adminChat(String memoryId, String message) {
        // 存储 memoryId → userId 映射到redis，因为在AI工具调用时，请求头中无法包含用户ID信息，需要显式传输携带
        String key = RedisKeyConstant.AI_CHAT_USER_ID + memoryId;
        RBucket<Long> rBucket = redissonClient.getBucket(key);
        Long userId = UserInfoContextHolder.getUserInfoContext().getUserId();
        rBucket.set(userId, Duration.ofDays(2)); // 过期时间和会话记忆过期时间一致

        // timeout 为0表示永不超时
        SseEmitter emitter = new SseEmitter(0L);
        TokenStream tokenStream = adminAssistant.adminChat(memoryId, message);

        tokenStream
                .onPartialResponse(partial -> { // 每个分片 token 到达时触发
                    try {
                        emitter.send(
                                SseEmitter.event()
                                        .name("token")
                                        .data(partial, UTF8_TEXT) // 发送该token
                        );
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .onCompleteResponse(resp -> { // 模型完整结束时触发
                    try {
                        String fullText = resp.aiMessage().text(); // 完整回复
                        emitter.send(
                                SseEmitter.event()
                                        .name("done")
                                        .data(fullText, UTF8_TEXT) // 发送完整内容
                        );
                    } catch (Exception ignored) {
                    } finally {
                        emitter.complete();
                    }
                })
                .onError(emitter::completeWithError)
                .start();

        return emitter;
    }

    /**
     * 创建新的会话
     *
     * @return 新的会话ID
     */
    @Override
    public ChatCreateVO createChat() {
        long nextId = snowflakeService.nextId();
        return new ChatCreateVO(String.valueOf(nextId));
    }


}
