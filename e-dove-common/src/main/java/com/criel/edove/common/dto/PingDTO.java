package com.criel.edove.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 连接测试请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PingDTO {

    private String message;

}
