package com.criel.edove.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户门店绑定接口请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreBindDTO {

    private Long storeId;

}
