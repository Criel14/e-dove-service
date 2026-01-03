package com.criel.edove.parcel.service;

import com.criel.edove.parcel.dto.CheckInDTO;
import com.criel.edove.parcel.dto.CheckOutDTO;
import com.criel.edove.parcel.entity.Parcel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.criel.edove.parcel.vo.CheckOutVO;

/**
 * <p>
 * 包裹信息表 服务类
 * </p>
 *
 * @author Criel
 * @since 2025-10-02
 */
public interface ParcelService extends IService<Parcel> {

    CheckOutVO checkOut(CheckOutDTO checkOutDTO);

    void checkIn(CheckInDTO checkInDTO);
}
