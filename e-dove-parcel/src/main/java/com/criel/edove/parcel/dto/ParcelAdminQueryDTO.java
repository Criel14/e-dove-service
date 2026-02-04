package com.criel.edove.parcel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 查询包裹信息接口的请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParcelAdminQueryDTO implements Serializable {

    /**
     * （必填）分页查询页码
     */
    private Integer pageNum;

    /**
     * （必填）分页查询每页大小
     */
    private Integer pageSize;

    /**
     * （选填，但管理端查询时前端必选）包裹状态：0=未入库、1=已入库、2=已取出、3=滞留、4=退回
     */
    private Integer status;

    /**
     * （选填）快递运单号
     */
    private String trackingNumber;

    /**
     * （选填）收件人手机号
     */
    private String recipientPhone;

    /**
     * （选填）要查询的时间段类型：有入库时间"inTime"，出库时间"outTime"
     */
    private String timeType;

    /**
     * （选填）要查询的时间段的开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    /**
     * （选填）要查询的时间段的结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

}
