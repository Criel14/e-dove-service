package com.criel.edove.store.dto;

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

    private int pageNum;

    private int pageSize;

}
