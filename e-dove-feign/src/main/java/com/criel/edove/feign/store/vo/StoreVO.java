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

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long managerUserId;

    private String managerPhone;

    private String storeName;

    private String addrProvince;

    private String addrCity;

    private String addrDistrict;

    private String addrDetail;

    private Integer status;

}
