package com.criel.edove.parcel.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户查询历史【已取出】包裹数量响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCountVO implements Serializable {

    /**
     * 历史【已取出】包裹数量
     * tip: 虽然极端情况下前端会溢出，但是系统约定 > 100 直接显示 "99+"，所以没问题
     */
    private Long count;

}
