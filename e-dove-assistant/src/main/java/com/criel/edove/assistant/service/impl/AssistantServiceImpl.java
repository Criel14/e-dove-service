package com.criel.edove.assistant.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.criel.edove.assistant.assistant.Assistant;
import com.criel.edove.assistant.assistant.StreamingAssistant;
import com.criel.edove.assistant.dto.AddressGenerateDTO;
import com.criel.edove.assistant.service.AssistantService;
import com.criel.edove.assistant.vo.AddressGenerateVO;
import com.criel.edove.assistant.vo.ChatCreateVO;
import com.criel.edove.common.service.SnowflakeService;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.encoders.UTF8;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
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
    private final StreamingAssistant streamingAssistant;
    private final SnowflakeService snowflakeService;

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
     */
    @Override
    public SseEmitter adminChat(String memoryId, String message) {
        // timeout 为0表示永不超时
        SseEmitter emitter = new SseEmitter(0L);
        TokenStream tokenStream = streamingAssistant.AdminChat(memoryId, message);

        tokenStream
                .onPartialResponse(partial -> { // 每个分片 token 到达时触发
                    try {
                        emitter.send(
                                SseEmitter.event()
                                        .name("token")
                                        .data(partial, UTF8_TEXT)
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
                                        .data(fullText, UTF8_TEXT)
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
        // TODO 要结合数据库
        long nextId = snowflakeService.nextId();
        return new ChatCreateVO(String.valueOf(nextId));
    }


}
