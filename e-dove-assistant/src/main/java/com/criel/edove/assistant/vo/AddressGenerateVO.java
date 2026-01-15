package com.criel.edove.assistant.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 大模型生成随机地址接口的响应参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressGenerateVO implements Serializable {

    private List<String> addresses;

}
