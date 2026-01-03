package com.criel.edove.store.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 包裹入库接口的相应参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParcelCheckInVO implements Serializable {

    /**
     * 取件码
     */
    private String pickCode;

}
