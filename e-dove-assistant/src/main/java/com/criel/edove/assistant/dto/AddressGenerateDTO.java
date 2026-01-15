package com.criel.edove.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 大模型生成随机地址接口的请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressGenerateDTO {

    /**
     * 需要生成的地址的数量
     */
    private Integer count;

    /**
     * 门店地址：省
     */
    private String storeAddrProvince;

    /**
     * 门店地址：市
     */
    private String storeAddrCity;

    /**
     * 门店地址：区
     */
    private String storeAddrDistrict;

}
