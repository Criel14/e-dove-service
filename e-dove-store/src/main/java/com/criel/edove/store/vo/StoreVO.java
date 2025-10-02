package com.criel.edove.store.vo;

import lombok.*;

import java.io.Serializable;

/**
 * 门店查询响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreVO implements Serializable {

    private Long id;

    private Long managerUserId;

    private String managerPhone;

    private String storeName;

    private String addrProvince;

    private String addrCity;

    private String addrDistrict;

    private String addrDetail;

    private Integer status;

}
