package com.criel.edove.assistant.assistant;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

import java.util.List;

/**
 * 大模型
 */
@AiService
public interface Assistant {

    @UserMessage("""
            请根据下面的省市区，随机生成{{count}}个标准的详细地址。
            省：{{province}}，市：{{city}}，区/县：{{district}}
            输出内容只包含地址，不带额外解释，以JSON格式输出，示例：
            ["百合园小区6栋701", "金园路102号"]
            """)
    String generateAddress(
            @V("count") Integer count,
            @V("province") String province,
            @V("city") String city,
            @V("district") String district
    );

}
