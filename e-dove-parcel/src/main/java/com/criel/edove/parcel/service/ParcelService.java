package com.criel.edove.parcel.service;

import com.criel.edove.common.result.PageResult;
import com.criel.edove.parcel.dto.CheckInDTO;
import com.criel.edove.parcel.dto.CheckOutDTO;
import com.criel.edove.parcel.dto.ParcelAdminQueryDTO;
import com.criel.edove.parcel.dto.ParcelUserQueryDTO;
import com.criel.edove.parcel.entity.Parcel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.criel.edove.parcel.vo.CheckOutVO;
import com.criel.edove.parcel.vo.ParcelVO;

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

    PageResult<ParcelVO> adminInfo(ParcelAdminQueryDTO parcelAdminQueryDTO);

    PageResult<ParcelVO> userInfo(ParcelUserQueryDTO parcelUserQueryDTO);

    void generate(Integer count);
}
