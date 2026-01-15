package com.criel.edove.assistant.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.criel.edove.assistant.assistant.Assistant;
import com.criel.edove.assistant.dto.AddressGenerateDTO;
import com.criel.edove.assistant.service.AssistantService;
import com.criel.edove.assistant.vo.AddressGenerateVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    /**
     * 生成指定数量的 门店所在区的 随机地址
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

}
