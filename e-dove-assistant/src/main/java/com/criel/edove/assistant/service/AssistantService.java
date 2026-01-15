package com.criel.edove.assistant.service;

import com.criel.edove.assistant.dto.AddressGenerateDTO;
import com.criel.edove.assistant.vo.AddressGenerateVO;

/**
 * 大模型调用服务
 *
 * @author Criel
 * @since 2026-01-15
 */
public interface AssistantService {

    AddressGenerateVO generateAddresses(AddressGenerateDTO addressGenerateDTO);

}
