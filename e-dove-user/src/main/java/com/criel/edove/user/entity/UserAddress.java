package com.criel.edove.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 存储用户的收货地址信息，支持设置默认地址
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Getter
@Setter
@TableName("user_address")
public class UserAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地址唯一标识ID
     */
    @TableId("address_id")
    private Long addressId;

    /**
     * 关联的用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 收件人姓名
     */
    @TableField("receiver_name")
    private String receiverName;

    /**
     * 收件人手机号
     */
    @TableField("receiver_phone")
    private String receiverPhone;

    /**
     * 国家
     */
    @TableField("country")
    private String country;

    /**
     * 省份
     */
    @TableField("province")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 区/县
     */
    @TableField("district")
    private String district;

    /**
     * 详细地址
     */
    @TableField("detail_address")
    private String detailAddress;

    /**
     * 邮政编码（可选）
     */
    @TableField("postal_code")
    private String postalCode;

    /**
     * 是否默认地址：0-否，1-是
     */
    @TableField("is_default")
    private Boolean isDefault;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
