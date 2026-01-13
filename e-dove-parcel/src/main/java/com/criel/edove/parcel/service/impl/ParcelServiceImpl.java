package com.criel.edove.parcel.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.criel.edove.common.context.UserInfoContextHolder;
import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.enumeration.ParcelStatusEnum;
import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.result.PageResult;
import com.criel.edove.common.result.Result;
import com.criel.edove.feign.store.client.StoreFeignClient;
import com.criel.edove.feign.store.dto.LayerReduceCountDTO;
import com.criel.edove.feign.store.dto.ParcelCheckInDTO;
import com.criel.edove.feign.store.vo.ParcelCheckInVO;
import com.criel.edove.feign.user.client.UserFeignClient;
import com.criel.edove.feign.user.vo.VerifyBarcodeVO;
import com.criel.edove.parcel.dto.CheckInDTO;
import com.criel.edove.parcel.dto.CheckOutDTO;
import com.criel.edove.parcel.dto.ParcelQueryDTO;
import com.criel.edove.parcel.entity.Parcel;
import com.criel.edove.parcel.mapper.ParcelMapper;
import com.criel.edove.parcel.service.ParcelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.criel.edove.parcel.util.TrackingNumberGenerator;
import com.criel.edove.parcel.vo.CheckOutVO;
import com.criel.edove.parcel.vo.ParcelVO;
import lombok.RequiredArgsConstructor;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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
     * 入库：管理员将包裹入库到本门店
     */
    @Override
    @GlobalTransactional
    public void checkIn(CheckInDTO checkInDTO) {
        // 查询当前用户所属门店
        Long storeId = getUserStoreId();

        // 查找包裹，并判断包裹是否属于当前的门店
        LambdaQueryWrapper<Parcel> parcelWrapper = new LambdaQueryWrapper<>();
        parcelWrapper.eq(Parcel::getTrackingNumber, checkInDTO.getTrackingNumber());
        Parcel parcel = parcelMapper.selectOne(parcelWrapper);
        if (parcel == null) {
            throw new BizException(ErrorCode.PARCEL_NOT_FOUND);
        }
        if (!Objects.equals(parcel.getStoreId(), storeId)) {
            throw new BizException(ErrorCode.PARCEL_STORE_MISMATCHED);
        }

        // 远程调用：选择合适的货架层，并生成取件码
        String pickCode = getPickCode(parcel);

        // 更新包裹表的status和in_time和pick_code
        parcel.setStatus(ParcelStatusEnum.IN_STORAGE.getCode());
        parcel.setInTime(LocalDateTime.now());
        parcel.setPickCode(pickCode);
        parcelMapper.updateById(parcel);
    }

    /**
     * 管理端分页查询门店包裹信息
     */
    @Override
    public PageResult<ParcelVO> adminInfo(ParcelQueryDTO parcelQueryDTO) {
        Integer status = parcelQueryDTO.getStatus();
        String trackingNumber = parcelQueryDTO.getTrackingNumber();
        String recipientPhone = parcelQueryDTO.getRecipientPhone();
        String timeType = parcelQueryDTO.getTimeType();
        LocalDateTime startTime = parcelQueryDTO.getStartTime();
        LocalDateTime endTime = parcelQueryDTO.getEndTime();

        LambdaQueryWrapper<Parcel> parcelWrapper = new LambdaQueryWrapper<>();
        // 包裹状态
        if (status != null) {
            parcelWrapper.eq(Parcel::getStatus, status);
        }
        // 快递运单号
        if (StrUtil.isEmpty(trackingNumber)) {
            parcelWrapper.like(Parcel::getTrackingNumber, trackingNumber);
        }
        // 收件人手机号
        if (StrUtil.isEmpty(recipientPhone)) {
            parcelWrapper.like(Parcel::getRecipientPhone, recipientPhone);
        }
        // 查询时间段
        if (StrUtil.isEmpty(timeType) && startTime != null && endTime != null) {
            switch (timeType) {
                // 入库时间
                case "in_time" -> parcelWrapper.between(Parcel::getInTime, startTime, endTime);
                // 出库时间
                case "out_time" -> parcelWrapper.between(Parcel::getOutTime, startTime, endTime);
            }
        }

        // 分页查询
        return selectParcelVOPageResult(
                parcelQueryDTO.getPageNum(),
                parcelQueryDTO.getPageSize(),
                parcelWrapper
        );
    }

    /**
     * 用户端分页查询门店包裹信息：查询用户近30日的包裹，不包含复杂的条件查询
     */
    @Override
    public PageResult<ParcelVO> userInfo(ParcelQueryDTO parcelQueryDTO) {
        // 获取用户手机号
        String phone = UserInfoContextHolder.getUserInfoContext().getPhone();
        // 查询近30日的包裹
        LambdaQueryWrapper<Parcel> parcelWrapper = new LambdaQueryWrapper<>();
        parcelWrapper.eq(Parcel::getRecipientPhone, phone)
                .between(Parcel::getCreateTime, LocalDateTime.now().minusDays(30), LocalDateTime.now());
        // 分页查询
        return selectParcelVOPageResult(
                parcelQueryDTO.getPageNum(),
                parcelQueryDTO.getPageSize(),
                parcelWrapper
        );
    }

    /**
     * 在数据库里生成指定数量的随机包裹（这算是一个调试方法，就不需要加分布式锁了）
     */
    @Override
    public void generate(Integer count) {
        // 生成随机运单号
        List<String> trackingNumbers = generateTrackingNumbers(count);
        // 抽取手机号
        List<String> phones = getPhones(count);

        // TODO 没写完
    }

    /**
     * 在数据库中抽取指定数量的手机号
     */
    private List<String> getPhones(Integer count) {
        Result<List<String>> result = userFeignClient.extractPhone(count);
        if (!result.getStatus()) {
            throw new BizException(ErrorCode.PARCEL_GENERATE_ERROR);
        }
        return result.getData();
    }

    /**
     * 生成指定数量的随机运单号
     */
    private List<String> generateTrackingNumbers(Integer count) {
        // 生成运单号
        List<String> resultList = new ArrayList<>();
        while (resultList.size() < count) {
            // 生成缺少的数量 * 2，增加命中概率
            int remaining = count - resultList.size();
            Set<String> set = new HashSet<>();
            while (set.size() < remaining * 2) {
                set.add(TrackingNumberGenerator.generateOne());
            }

            // 查找数据库
            LambdaQueryWrapper<Parcel> parcelWrapper = new LambdaQueryWrapper<>();
            parcelWrapper.in(Parcel::getTrackingNumber, set);
            List<Parcel> parcels = parcelMapper.selectList(parcelWrapper);

            // 过滤掉存在的
            parcels.forEach(parcel -> set.remove(parcel.getTrackingNumber()));

            resultList.addAll(set);
        }

        // 可能会多生成，去掉重复的
        return resultList.subList(0, count);
    }

    /**
     * 按照给定条件分页查询包裹，并封装成VO
     */
    private PageResult<ParcelVO> selectParcelVOPageResult(Integer pageNum, Integer pageSize, LambdaQueryWrapper<Parcel> parcelWrapper) {
        IPage<Parcel> page = new Page<>(pageNum, pageSize);
        IPage<Parcel> parcelPage = parcelMapper.selectPage(page, parcelWrapper);
        List<Parcel> parcels = parcelPage.getRecords();
        List<ParcelVO> parcelVOs = parcels.stream()
                .map(parcel -> {
                            ParcelVO vo = new ParcelVO();
                            BeanUtils.copyProperties(parcel, vo);
                            return vo;
                        }
                )
                .toList();
        return new PageResult<>(parcelVOs, parcelPage.getTotal());
    }

    /**
     * 远程调用：选择合适的货架层，并生成取件码
     *
     * @param parcel 要入库的包裹信息
     * @return 取件码
     */
    private String getPickCode(Parcel parcel) {
        ParcelCheckInDTO parcelCheckInDTO = new ParcelCheckInDTO();
        BeanUtils.copyProperties(parcel, parcelCheckInDTO);
        Result<ParcelCheckInVO> result = storeFeignClient.parcelCheckIn(parcelCheckInDTO);
        if (!result.getStatus()) {
            throw new BizException(result.getCode(), result.getMessage());
        }
        ParcelCheckInVO parcelCheckInVO = result.getData();
        if (parcelCheckInVO == null) {
            throw new BizException(ErrorCode.SYSTEM_ERROR);
        }
        return parcelCheckInVO.getPickCode();
    }

    /**
     * 查询用户所属门店
     *
     * @return 用户所属的门店ID
     */
    private Long getUserStoreId() {
        // 查询当前用户所属门店
        Result<Long> result = userFeignClient.getUserStoreId();
        if (!result.getStatus()) {
            throw new BizException(result.getCode(), result.getMessage());
        }
        return result.getData();
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
