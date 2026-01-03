package com.criel.edove.parcel.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 出库接口的响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutVO implements Serializable {

    /**
     * 剩余包裹的数量
     */
    Long remaining;

}
