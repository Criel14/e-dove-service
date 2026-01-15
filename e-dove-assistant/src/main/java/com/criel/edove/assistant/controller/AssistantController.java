package com.criel.edove.assistant.controller;

import com.criel.edove.assistant.dto.AddressGenerateDTO;
import com.criel.edove.assistant.service.AssistantService;
import com.criel.edove.assistant.vo.AddressGenerateVO;
import com.criel.edove.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param addressGenerateDTO 门店的省市区地址 + 要生成的地址数量
     */
    @PostMapping("/address")
    public Result<AddressGenerateVO> generateAddresses(AddressGenerateDTO addressGenerateDTO) {
        return Result.success(assistantService.generateAddresses(addressGenerateDTO));
    }

}
