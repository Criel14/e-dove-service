package com.criel.edove.feign.parcel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户查询包裹信息接口的请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParcelUserQueryDTO implements Serializable {

    /**
     * （AI工具调用时需要手动传参）用户ID
     */
    private Long userId;

    /**
     * （必填）分页查询页码
     */
    private Integer pageNum;

    /**
     * （必填）分页查询每页大小
     */
    private Integer pageSize;

    /**
     * （选填）包裹状态：0=未入库、1=已入库、2=已取出、3=滞留、4=退回
     */
    private Integer status;

}
