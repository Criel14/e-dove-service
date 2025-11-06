package com.criel.edove.store.dto;

import lombok.*;

/**
 * 创建/修改门店信息接口请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreDTO {

    // 修改时id不为null
    private Long id;

    private String storeName;

    private Long managerUserId;

    private String managerPhone;

    private String addrProvince;

    private String addrCity;

    private String addrDistrict;

    private String addrDetail;

    // 修改门店营业状态时不为null
    private Integer status;

}
