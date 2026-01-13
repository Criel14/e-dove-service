package com.criel.edove.parcel.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 包裹信息响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParcelVO {

    /**
     * 雪花算法生成的包裹唯一 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 快递运单号
     */
    private String trackingNumber;

    /**
     * 收件人手机号
     */
    private String recipientPhone;

    /**
     * 收件人地址——省
     */
    private String recipientAddrProvince;

    /**
     * 收件人地址——市
     */
    private String recipientAddrCity;

    /**
     * 收件人地址——区／县
     */
    private String recipientAddrDistrict;

    /**
     * 收件人地址——详细地址
     */
    private String recipientAddrDetail;

    /**
     * 包裹宽度（单位：cm）
     */
    private BigDecimal width;

    /**
     * 包裹高度（单位：cm）
     */
    private BigDecimal height;

    /**
     * 包裹长度（单位：cm）
     */
    private BigDecimal length;

    /**
     * 包裹重量（单位：kg）
     */
    private BigDecimal weight;

    /**
     * 送至门店 ID
     */
    private Long storeId;

    /**
     * 6 位取件码（已入库后生成，包含货架／层信息）
     */
    private String pickCode;

    /**
     * 包裹状态（整型）：0=未入库/新包裹、1=已入库、2=已取出、3=滞留、4=退回
     */
    private Integer status;

    /**
     * 包裹入库时间
     */
    private LocalDateTime inTime;

    /**
     * 包裹取件／出库时间
     */
    private LocalDateTime outTime;

}
