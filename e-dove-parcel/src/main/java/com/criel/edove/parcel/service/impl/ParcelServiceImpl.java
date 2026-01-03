package com.criel.edove.parcel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.enumeration.ParcelStatusEnum;
import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.result.Result;
import com.criel.edove.feign.store.client.StoreFeignClient;
import com.criel.edove.feign.store.dto.LayerReduceCountDTO;
import com.criel.edove.feign.user.client.UserFeignClient;
import com.criel.edove.feign.user.vo.VerifyBarcodeVO;
import com.criel.edove.parcel.dto.CheckOutDTO;
import com.criel.edove.parcel.entity.Parcel;
import com.criel.edove.parcel.mapper.ParcelMapper;
import com.criel.edove.parcel.service.ParcelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.criel.edove.parcel.vo.CheckOutVO;
import lombok.RequiredArgsConstructor;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 包裹信息表 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2025-10-02
 */
@Service
@RequiredArgsConstructor
public class ParcelServiceImpl extends ServiceImpl<ParcelMapper, Parcel> implements ParcelService {

    private final UserFeignClient userFeignClient;
    private final StoreFeignClient storeFeignClient;
    private final ParcelMapper parcelMapper;

    /**
     * 出库：机器调用或管理员手动出库
     *
     * @return 返回剩余的包裹数量
     */
    @Override
    @GlobalTransactional
    public CheckOutVO checkOut(CheckOutDTO checkOutDTO) {
        String phone = verifyBarcodeAndGetPhone(checkOutDTO);
        String trackingNumber = checkOutDTO.getTrackingNumber();
        // TODO 机器ID可以用来做审计：parcel表加上一个字段
        Long machineId = checkOutDTO.getMachineId();

        // 更新包裹
        Parcel parcel = updateParcel(phone, trackingNumber);

        // 远程调用：扣减包裹所在货架层的当前包裹数
        layerReduceCount(parcel);

        // 查询剩余包裹数量
        LambdaQueryWrapper<Parcel> parcelWrapper = new LambdaQueryWrapper<>();
        parcelWrapper.eq(Parcel::getStatus, ParcelStatusEnum.IN_STORAGE.getCode());
        parcelWrapper.eq(Parcel::getRecipientPhone, phone);
        Long count = parcelMapper.selectCount(parcelWrapper);
        return new CheckOutVO(count);
    }

    /**
     * 更新包裹表的status和out_time
     * TODO 更新出库审计（机器ID）
     */
    private Parcel updateParcel(String phone, String trackingNumber) {
        // 查询包裹
        LambdaQueryWrapper<Parcel> parcelWrapper = new LambdaQueryWrapper<>();
        parcelWrapper.eq(Parcel::getRecipientPhone, phone).eq(Parcel::getTrackingNumber, trackingNumber);
        Parcel parcel = parcelMapper.selectOne(parcelWrapper);
        if (parcel == null) {
            throw new BizException(ErrorCode.PARCEL_NOT_FOUND);
        }
        // 校验包裹状态是否为“1-已入库”
        if (!Objects.equals(parcel.getStatus(), ParcelStatusEnum.IN_STORAGE.getCode())) {
            throw new BizException(ErrorCode.PARCEL_NOT_IN_STORAGE);
        }
        // 更新包裹表的status和out_time
        parcel.setStatus(ParcelStatusEnum.OUT_STORAGE.getCode());
        parcel.setOutTime(LocalDateTime.now());
        return parcel;
    }

    /**
     * 远程调用：扣减包裹所在货架层的当前包裹数
     */
    private void layerReduceCount(Parcel parcel) {
        Long storeId = parcel.getStoreId();
        String pickCode = parcel.getPickCode();
        String[] codes = pickCode.split("-");
        Integer shelfNo = Integer.valueOf(codes[0]);
        Integer layerNo = Integer.valueOf(codes[1]);
        Result<Void> result = storeFeignClient.layerReduceCount(new LayerReduceCountDTO(storeId, shelfNo, layerNo));
        if (!result.getStatus()) {
            throw new BizException(result.getCode(), result.getMessage());
        }
    }

    /**
     * （远程调用）验证身份码，并获取手机号
     */
    private String verifyBarcodeAndGetPhone(CheckOutDTO checkOutDTO) {
        // 通过用户身份码获取用户手机号
        Result<VerifyBarcodeVO> result = userFeignClient.verifyBarcode(checkOutDTO.getIdentityCode());
        if (!result.getStatus()) {
            throw new BizException(result.getCode(), result.getMessage());
        }
        VerifyBarcodeVO verifyBarcodeVO = result.getData();

        // 如果status是true，但是数据为空，则说明有问题
        if (verifyBarcodeVO == null) {
            throw new BizException(ErrorCode.SYSTEM_ERROR);
        }

        return verifyBarcodeVO.getPhone();
    }
}
