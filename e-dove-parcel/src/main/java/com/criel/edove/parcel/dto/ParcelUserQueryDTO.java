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
public class ParcelUserQueryDTO implements Serializable {

    /**
     * （必填）分页查询页码
     */
    private Integer pageNum;

    /**
     * （必填）分页查询每页大小
     */
    private Integer pageSize;

}
