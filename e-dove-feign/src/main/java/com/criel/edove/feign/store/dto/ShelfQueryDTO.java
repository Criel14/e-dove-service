package com.criel.edove.feign.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分页查询货架的请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShelfQueryDTO implements Serializable {

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 在AI工具调用时，请求头中无法包含用户ID信息，需要显式传输携带
     */
    private Long userId;

}
