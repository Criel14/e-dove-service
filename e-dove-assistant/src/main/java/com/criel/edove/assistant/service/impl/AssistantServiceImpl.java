package com.criel.edove.assistant.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.criel.edove.assistant.assistant.AdminAssistant;
import com.criel.edove.assistant.assistant.Assistant;
import com.criel.edove.assistant.assistant.UserAssistant;
import com.criel.edove.assistant.dto.AddressGenerateDTO;
import com.criel.edove.assistant.service.AssistantService;
import com.criel.edove.assistant.vo.AddressGenerateVO;
import com.criel.edove.assistant.vo.ChatCreateVO;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.context.UserInfoContextHolder;
import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BizException;
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
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

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
    private final UserAssistant userAssistant;
    private final SnowflakeService snowflakeService;
    private final RedissonClient redissonClient;

    private static final MediaType UTF8_JSON = new MediaType("application", "json", StandardCharsets.UTF_8);

    /**
     * 生成指定数量的 门店所在区的 随机地址
     *
     * @param addressGenerateDTO 门店的省市区地址 + 要生成的地址数量
     */
    @Override
    public AddressGenerateVO generateAddresses(AddressGenerateDTO addressGenerateDTO) {
        // 随机生成一个 memoryId
        String randomMemoryId = UUID.randomUUID()
                .toString();

        // 调用大模型生成地址数组
        String result = assistant.generateAddress(
                randomMemoryId,
                addressGenerateDTO.getCount(),
                addressGenerateDTO.getStoreAddrProvince(),
                addressGenerateDTO.getStoreAddrCity(),
                addressGenerateDTO.getStoreAddrDistrict()
        );

        // 格式化大模型返回的字符串
        try {
            JSONArray jsonArray = JSONUtil.parseArray(result);
            List<String> addresses = jsonArray.toList(String.class);
            return new AddressGenerateVO(addresses);
        } catch (Exception e) {
            // 大模型返回的不是JSON格式
            throw new BizException(ErrorCode.MESSAGE_PARSE_JSON_ERROR);
        }
    }

    /**
     * 管理端AI对话（SSE）
     *
     * @return SSE连接对象，服务端不断推新的token给前端
     */
    @Override
    public SseEmitter adminChat(String memoryId, String message) {
        // 存储 memoryId → userId 映射到redis，因为在AI工具调用时，请求头中无法包含用户ID信息，需要显式传输携带
        Long userId = UserInfoContextHolder.getUserInfoContext()
                .getUserId();
        String key = RedisKeyConstant.AI_CHAT_USER_ID + memoryId;
        RBucket<Long> rBucket = redissonClient.getBucket(key);
        rBucket.set(userId, Duration.ofDays(2)); // 过期时间和会话记忆过期时间一致

        // timeout 为0表示永不超时
        SseEmitter emitter = new SseEmitter(0L);
        // 统计序列编号
        AtomicLong seq = new AtomicLong(0);

        // AI对话
        TokenStream tokenStream = adminAssistant.adminChat(memoryId, message);
        tokenStream
                .onPartialResponse(partial -> { // 每个分片 token 到达时触发
                    try {
                        Map<String, String> dataMap = Map.of(
                                "seq", String.valueOf(seq.incrementAndGet()),
                                "content", partial == null ? "" : partial
                        );
                        emitter.send(
                                SseEmitter.event()
                                        .name("token")
                                        .data(dataMap, UTF8_JSON) // 发送包含本次token的数据
                        );
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .onCompleteResponse(resp -> { // 模型完整结束时触发
                    try {
                        Map<String, Boolean> dataMap = Map.of(
                                "finish", true
                        );
                        emitter.send(
                                SseEmitter.event()
                                        .name("done")
                                        .data(dataMap, UTF8_JSON) // 发送结束信息
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

    /**
     * 用户端 WebSocket 流式聊天
     * tip：Consumer类表示：有参无返回值的函数，类似Supplier和Function
     *
     * @param onToken token 分片回调（流式输出核心回调）。
     * @param onDone  完成回调。
     * @param onError 异常回调。
     */
    @Override
    public void userChatStream(
            Long userId,
            String memoryId,
            String message,
            Consumer<String> onToken,
            Runnable onDone,
            Consumer<Throwable> onError) {
        // 存储 memoryId → userId 映射到redis，因为在AI工具调用时，请求头中无法包含用户ID信息，需要显式传输携带
        String key = RedisKeyConstant.AI_CHAT_USER_ID + memoryId;
        RBucket<Long> rBucket = redissonClient.getBucket(key);
        rBucket.set(userId, Duration.ofDays(2)); // 过期时间和会话记忆过期时间一致

        // AI对话
        TokenStream tokenStream = userAssistant.userChat(memoryId, message);
        tokenStream
                .onPartialResponse(
                        partial -> onToken.accept(partial == null ? "" : partial)
                )
                .onCompleteResponse(
                        resp -> onDone.run()
                )
                .onError(onError)
                .start();
    }
}