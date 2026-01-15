package com.criel.edove.parcel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 生成包裹接口请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateDTO implements Serializable {

    /**
     * 范围是1 - 30
     */
    Integer count;

}
