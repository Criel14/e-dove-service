package com.criel.edove.feign.store.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 门店查询响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreVO implements Serializable {

    /**
     * 门店ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 门店管理员ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long managerUserId;

    /**
     * 门店管理员手机号
     */
    private String managerPhone;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店地址-省
     */
    private String addrProvince;

    /**
     * 门店地址-市
     */
    private String addrCity;

    /**
     * 门店地址-区
     */
    private String addrDistrict;

    /**
     * 门店地址-详细地址
     */
    private String addrDetail;

    /**
     * 门店状态
     */
    private Integer status;

}
