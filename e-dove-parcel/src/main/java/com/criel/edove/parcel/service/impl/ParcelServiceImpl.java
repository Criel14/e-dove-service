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
import com.criel.edove.common.service.SnowflakeService;
import com.criel.edove.feign.assistant.client.AssistantFeignClient;
import com.criel.edove.feign.assistant.dto.AddressGenerateDTO;
import com.criel.edove.feign.assistant.vo.AddressGenerateVO;
import com.criel.edove.feign.store.client.StoreFeignClient;
import com.criel.edove.feign.store.dto.LayerReduceCountDTO;
import com.criel.edove.feign.store.dto.ParcelCheckInDTO;
import com.criel.edove.feign.store.vo.ParcelCheckInVO;
import com.criel.edove.feign.store.vo.StoreVO;
import com.criel.edove.feign.user.client.UserFeignClient;
import com.criel.edove.feign.user.vo.VerifyBarcodeVO;
import com.criel.edove.parcel.dto.CheckInDTO;
import com.criel.edove.parcel.dto.CheckOutDTO;
import com.criel.edove.parcel.dto.ParcelAdminQueryDTO;
import com.criel.edove.parcel.dto.ParcelUserQueryDTO;
import com.criel.edove.parcel.entity.Parcel;
import com.criel.edove.parcel.mapper.ParcelMapper;
import com.criel.edove.parcel.service.ParcelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.criel.edove.parcel.util.TrackingNumberGenerator;
import com.criel.edove.parcel.vo.CheckOutVO;
import com.criel.edove.parcel.vo.ParcelVO;
import com.criel.edove.parcel.vo.UserCountVO;
import lombok.RequiredArgsConstructor;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final AssistantFeignClient assistantFeignClient;
    private final ParcelMapper parcelMapper;
    private final SnowflakeService snowflakeService;

    private final Random random = new Random();

    /**
     * 出库：机器调用或管理员手动出库
     *
     * @return 返回剩余的包裹数量
     */
    @Override
    @GlobalTransactional
    public CheckOutVO checkOut(CheckOutDTO checkOutDTO) {
        // TODO 机器ID可以用来做审计：parcel表加上一个字段
        Long machineId = checkOutDTO.getMachineId();
        String trackingNumber = checkOutDTO.getTrackingNumber();
        String identityCode = checkOutDTO.getIdentityCode();

        // 获取用户手机号
        String phone;
        if (StrUtil.isNotBlank(checkOutDTO.getRecipientPhone())) {
            // 【管理员出库】：直接获取收件人手机号
            phone = checkOutDTO.getRecipientPhone();
        } else {
            // 【用户出库】：验证身份码，并获取手机号
            phone = verifyBarcodeAndGetPhone(identityCode);
        }

        // TODO 消息队列异步削峰

        // 更新包裹
        Parcel parcel = updateParcel(phone, trackingNumber, machineId);

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
     * TODO （消息队列）推送入库消息给用户
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
    public PageResult<ParcelVO> adminInfo(ParcelAdminQueryDTO parcelAdminQueryDTO) {
        Integer status = parcelAdminQueryDTO.getStatus();
        String trackingNumber = parcelAdminQueryDTO.getTrackingNumber();
        String recipientPhone = parcelAdminQueryDTO.getRecipientPhone();
        String timeType = parcelAdminQueryDTO.getTimeType();
        LocalDate startTime = parcelAdminQueryDTO.getStartTime();
        LocalDate endTime = parcelAdminQueryDTO.getEndTime();

        LambdaQueryWrapper<Parcel> parcelWrapper = new LambdaQueryWrapper<>();
        // 所属门店
        Long storeId = getUserStoreId();
        parcelWrapper.eq(Parcel::getStoreId, storeId);
        // 包裹状态
        if (status != null) {
            parcelWrapper.eq(Parcel::getStatus, status);
        }
        // 快递运单号
        if (StrUtil.isNotEmpty(trackingNumber)) {
            parcelWrapper.like(Parcel::getTrackingNumber, trackingNumber);
        }
        // 收件人手机号
        if (StrUtil.isNotEmpty(recipientPhone)) {
            parcelWrapper.like(Parcel::getRecipientPhone, recipientPhone);
        }
        // 查询时间段
        if (StrUtil.isNotEmpty(timeType) && startTime != null && endTime != null) {
            switch (timeType) {
                // 入库时间
                case "inTime" -> parcelWrapper.between(Parcel::getInTime, startTime, endTime);
                // 出库时间
                case "outTime" -> parcelWrapper.between(Parcel::getOutTime, startTime, endTime);
            }
        }

        // 分页查询
        return selectParcelVOPageResult(
                parcelAdminQueryDTO.getPageNum(),
                parcelAdminQueryDTO.getPageSize(),
                parcelWrapper
        );
    }

    /**
     * 用户端分页查询门店包裹信息：查询用户近30日的包裹，不包含复杂的条件查询
     */
    @Override
    public PageResult<ParcelVO> userInfo(ParcelUserQueryDTO parcelUserQueryDTO) {
        // 获取用户手机号
        String phone = UserInfoContextHolder.getUserInfoContext().getPhone();
        // 查询近30日的包裹
        LambdaQueryWrapper<Parcel> parcelWrapper = new LambdaQueryWrapper<>();
        parcelWrapper.eq(Parcel::getRecipientPhone, phone)
                .between(Parcel::getCreateTime, LocalDateTime.now().minusDays(30), LocalDateTime.now());
        // 分页查询
        return selectParcelVOPageResult(
                parcelUserQueryDTO.getPageNum(),
                parcelUserQueryDTO.getPageSize(),
                parcelWrapper
        );
    }

    /**
     * 在数据库里生成指定数量的随机包裹（这算是一个调试方法，就不需要加分布式锁了）
     * 暂定为：生成的包裹全部送到【用户所在的门店】
     */
    @Override
    public void generate(Integer count) {
        if (count > 30) {
            throw new BizException(ErrorCode.GENERATE_PARCEL_COUNT_TOO_LARGE);
        }
        // 生成随机运单号
        List<String> trackingNumbers = generateTrackingNumbers(count);
        // 抽取手机号
        List<String> phones = getPhones(count);
        // 获取用户所在门店信息
        StoreVO storeVO = getStoreInfoByUser();
        // 调用 LLM 生成 count 个随机详细地址
        List<String> addresses = generateAddresses(
                count,
                storeVO.getAddrProvince(),
                storeVO.getAddrCity(),
                storeVO.getAddrDistrict()
        );

        // 循环生成包裹
        List<Parcel> parcels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Parcel parcel = new Parcel();
            // 包裹ID、运单号、收件人手机号
            parcel.setId(snowflakeService.nextId());
            parcel.setTrackingNumber(trackingNumbers.get(i));
            parcel.setRecipientPhone(phones.get(i));
            // 包裹地址
            parcel.setRecipientAddrProvince(storeVO.getAddrProvince());
            parcel.setRecipientAddrCity(storeVO.getAddrCity());
            parcel.setRecipientAddrDistrict(storeVO.getAddrDistrict());
            parcel.setRecipientAddrDetail(addresses.get(i));
            // 包裹大小：长宽高重
            parcel.setWeight(new BigDecimal(random.nextDouble(0, 10)));
            parcel.setLength(new BigDecimal(random.nextDouble(0, 50)));
            parcel.setWidth(new BigDecimal(random.nextDouble(0, 50)));
            parcel.setHeight(new BigDecimal(random.nextDouble(0, 50)));
            // 门店ID
            parcel.setStoreId(storeVO.getId());
            // 包裹状态
            parcel.setStatus(ParcelStatusEnum.NEW_PARCEL.getCode());

            parcels.add(parcel);
        }

        // 批量插入
        parcelMapper.insert(parcels);
    }

    /**
     * 运单号查包裹
     */
    @Override
    public ParcelVO queryByTrackingNumber(String trackingNumber) {
        LambdaQueryWrapper<Parcel> parcelWrapper = new LambdaQueryWrapper<>();
        parcelWrapper.eq(Parcel::getTrackingNumber, trackingNumber);
        Parcel parcel = parcelMapper.selectOne(parcelWrapper);
        if (parcel == null) {
            throw new BizException(ErrorCode.PARCEL_NOT_FOUND);
        }
        ParcelVO parcelVO = new ParcelVO();
        BeanUtils.copyProperties(parcel, parcelVO);
        return parcelVO;
    }

    /**
     * 查询用户历史【已取出】包裹数量
     */
    @Override
    public UserCountVO userCount() {
        String phone = UserInfoContextHolder.getUserInfoContext().getPhone();
        LambdaQueryWrapper<Parcel> parcelWrapper = new LambdaQueryWrapper<>();
        parcelWrapper.eq(Parcel::getRecipientPhone, phone)
                .eq(Parcel::getStatus, ParcelStatusEnum.OUT_STORAGE.getCode());
        Long count = parcelMapper.selectCount(parcelWrapper);
        return new UserCountVO(count);
    }

    /**
     * 获取用户所在门店信息
     */
    private StoreVO getStoreInfoByUser() {
        Result<StoreVO> storeVOResult = storeFeignClient.getStoreInfoByUser();
        if (!storeVOResult.getStatus()) {
            throw new BizException(storeVOResult.getCode(), storeVOResult.getMessage());
        }
        return storeVOResult.getData();
    }

    /**
     * 调用 LLM 生成 count 个随机详细地址
     */
    private List<String> generateAddresses(Integer count, String province, String city, String district) {
        AddressGenerateDTO addressGenerateDTO = new AddressGenerateDTO(
                count,
                province,
                city,
                district
        );
        // 远程调用
        Result<AddressGenerateVO> addressGenerateVOResult = assistantFeignClient.generateAddresses(addressGenerateDTO);
        if (!addressGenerateVOResult.getStatus()) {
            throw new BizException(addressGenerateVOResult.getCode(), addressGenerateVOResult.getMessage());
        }
        AddressGenerateVO addressGenerateVO = addressGenerateVOResult.getData();
        return addressGenerateVO.getAddresses();
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
     * 更新包裹表的status, out_time, out_machine_id
     */
    private Parcel updateParcel(String phone, String trackingNumber, Long machineId) {
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
        // 更新包裹表的out_machine_id
        if (machineId != null) {
            parcel.setOutMachineId(machineId);
        }

        parcelMapper.updateById(parcel);
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
    private String verifyBarcodeAndGetPhone(String identityCode) {
        // 通过用户身份码获取用户手机号
        Result<VerifyBarcodeVO> result = userFeignClient.verifyBarcode(identityCode);
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
