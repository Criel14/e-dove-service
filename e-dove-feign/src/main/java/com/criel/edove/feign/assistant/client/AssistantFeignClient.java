package com.criel.edove.feign.assistant.client;

import com.criel.edove.common.result.Result;
import com.criel.edove.feign.assistant.dto.AddressGenerateDTO;
import com.criel.edove.feign.assistant.vo.AddressGenerateVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * e-dove-assistant模块的远程调用
 */
@FeignClient("e-dove-assistant")
public interface AssistantFeignClient {

    /**
     * 生成指定数量的 门店所在区的 随机地址
     * @param addressGenerateDTO 门店的省市区地址 + 要生成的地址数量
     */
    @PostMapping("/assistant/address")
    Result<AddressGenerateVO> generateAddresses(AddressGenerateDTO addressGenerateDTO);

}
