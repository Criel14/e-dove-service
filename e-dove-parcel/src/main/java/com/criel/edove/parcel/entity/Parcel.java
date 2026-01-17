package com.criel.edove.parcel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 包裹信息表
 * </p>
 *
 * @author Criel
 * @since 2025-10-02
 */
@Getter
@Setter
@ToString
@TableName("parcel")
public class Parcel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 雪花算法生成的包裹唯一 ID
     */
    @TableId("id")
    private Long id;

    /**
     * 快递运单号
     */
    @TableField("tracking_number")
    private String trackingNumber;

    /**
     * 收件人手机号
     */
    @TableField("recipient_phone")
    private String recipientPhone;

    /**
     * 收件人地址——省
     */
    @TableField("recipient_addr_province")
    private String recipientAddrProvince;

    /**
     * 收件人地址——市
     */
    @TableField("recipient_addr_city")
    private String recipientAddrCity;

    /**
     * 收件人地址——区／县
     */
    @TableField("recipient_addr_district")
    private String recipientAddrDistrict;

    /**
     * 收件人地址——详细地址
     */
    @TableField("recipient_addr_detail")
    private String recipientAddrDetail;

    /**
     * 包裹宽度（单位：cm）
     */
    @TableField("width")
    private BigDecimal width;

    /**
     * 包裹高度（单位：cm）
     */
    @TableField("height")
    private BigDecimal height;

    /**
     * 包裹长度（单位：cm）
     */
    @TableField("length")
    private BigDecimal length;

    /**
     * 包裹重量（单位：kg）
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 送至门店 ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 6 位取件码（已入库后生成，包含货架／层信息）
     */
    @TableField("pick_code")
    private String pickCode;

    /**
     * 包裹状态（整型）：0=新包裹、1=已入库、2=已取出、3=滞留、4=退回
     */
    @TableField("status")
    private Integer status;

    /**
     * 包裹入库时间
     */
    @TableField("in_time")
    private LocalDateTime inTime;

    /**
     * 包裹取件／出库时间
     */
    @TableField("out_time")
    private LocalDateTime outTime;

    /**
     * 包裹出库的机器ID
     */
    @TableField("out_machine_id")
    private Long outMachineId;

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
