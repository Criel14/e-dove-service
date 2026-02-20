package com.criel.edove.assistant.controller;

import com.criel.edove.assistant.dto.AddressGenerateDTO;
import com.criel.edove.assistant.dto.AdminChatDTO;
import com.criel.edove.assistant.service.AssistantService;
import com.criel.edove.assistant.vo.AddressGenerateVO;
import com.criel.edove.assistant.vo.ChatCreateVO;
import com.criel.edove.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 大模型调用 Controller
 *
 * @author Criel
 * @since 2026-01-15
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/assistant")
public class AssistantController {

    private final AssistantService assistantService;

    /**
     * 生成指定数量的 门店所在区的 随机地址
     *
     * @param addressGenerateDTO 门店的省市区地址 + 要生成的地址数量
     */
    @PostMapping("/address")
    public Result<AddressGenerateVO> generateAddresses(@RequestBody AddressGenerateDTO addressGenerateDTO) {
        return Result.success(assistantService.generateAddresses(addressGenerateDTO));
    }

    /**
     * 新建AI会话
     * @return 新的会话ID
     */
    @GetMapping("/chat/create")
    public Result<ChatCreateVO> createChat() {
        return Result.success(assistantService.createChat());
    }

    /**
     * 管理端AI聊天接口
     * MediaType.TEXT_EVENT_STREAM_VALUE：表示这是个SSE流，告知前端
     * @param adminChatDTO 会话ID + 聊天信息
     */
    @PostMapping(value = "/admin/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter adminChat(@RequestBody AdminChatDTO adminChatDTO) {
        return assistantService.adminChat(adminChatDTO.getMemoryId(), adminChatDTO.getMessage());
    }
}
