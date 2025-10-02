package com.criel.edove.store.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 门店信息表
 * </p>
 *
 * @author Criel
 * @since 2025-10-02
 */
@Getter
@Setter
@ToString
@TableName("store")
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 雪花算法生成的门店唯一 ID
     */
    @TableId("id")
    private Long id;

    /**
     * 店长用户 ID
     */
    @TableField("manager_user_id")
    private Long managerUserId;

    /**
     * 店长手机号
     */
    @TableField("manager_phone")
    private String managerPhone;

    /**
     * 门店名称
     */
    @TableField("store_name")
    private String storeName;

    /**
     * 门店地址——省
     */
    @TableField("addr_province")
    private String addrProvince;

    /**
     * 门店地址——市
     */
    @TableField("addr_city")
    private String addrCity;

    /**
     * 门店地址——区／县
     */
    @TableField("addr_district")
    private String addrDistrict;

    /**
     * 门店详细地址
     */
    @TableField("addr_detail")
    private String addrDetail;

    /**
     * 门店状态（整型）：如 1=营业、2=休息、3=注销
     */
    @TableField("status")
    private Integer status;

    /**
     * 记录创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 记录最后更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
