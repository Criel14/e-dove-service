package com.criel.edove.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 存放 storeId 的 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreIdDTO implements Serializable {

    Long storeId;

}
