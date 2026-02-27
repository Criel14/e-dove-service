package com.criel.edove.assistant.tool;

import cn.hutool.core.util.StrUtil;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.result.PageResult;
import com.criel.edove.common.result.Result;
import com.criel.edove.common.result.ToolResult;
import com.criel.edove.feign.parcel.client.ParcelFeignClient;
import com.criel.edove.feign.parcel.dto.ParcelAdminQueryDTO;
import com.criel.edove.feign.parcel.vo.ParcelVO;
import com.criel.edove.feign.store.client.StoreFeignClient;
import com.criel.edove.feign.store.dto.ShelfQueryDTO;
import com.criel.edove.feign.store.vo.ShelfAndLayerVO;
import com.criel.edove.feign.store.vo.StoreVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 管理端AI聊天工具
 */
@Component
@RequiredArgsConstructor
public class AdminChatTool {

    private final ParcelFeignClient parcelFeignClient;
    private final StoreFeignClient storeFeignClient;

    private final RedissonClient redissonClient;

    /**
     * 从redis中，基于memoryId，获取用户ID
     * tip：在工具函数调用时，请求头里不会携带用户信息，所以需要手动传参
     */
    private Long getUserId(String memoryId) {
        String key = RedisKeyConstant.AI_CHAT_USER_ID + memoryId;
        RBucket<Long> rBucket = redissonClient.getBucket(key);
        if (!rBucket.isExists()) {
            // 获取用户ID失败
            throw new RuntimeException("系统异常，请联系管理员");
        }
        return rBucket.get();
    }

    @Tool("""
            获取当前时间。
            这个功能是给你需要时使用的，而不属于系统业务，不需要呈现给用户。
            (本工具返回的data应不为null)
            """)
    public ToolResult<LocalDateTime> queryCurrentTime() {
        return ToolResult.success(LocalDateTime.now());
    }

    @Tool("""
            根据运单号查询包裹信息；支持在所有包裹中查询。
            (本工具返回的data应不为null)
            返回数据字段说明：
              - id：包裹ID
              - trackingNumber：快递运单号
              - recipientPhone：收件人手机号
              - recipientAddrProvince：收件地址-省
              - recipientAddrCity：收件地址-市
              - recipientAddrDistrict：收件地址-区/县
              - recipientAddrDetail：收件地址-详细地址
              - width：包裹宽度（cm）
              - height：包裹高度（cm）
              - length：包裹长度（cm）
              - weight：包裹重量（kg）
              - storeId：门店ID
              - storeName：门店名称
              - pickCode：取件码（入库后生成）
              - status：包裹状态（0未入库、1已入库、2已取出、3滞留、4退回）
              - inTime：入库时间（yyyy-MM-dd HH:mm:ss）
              - outTime：出库/取件时间（yyyy-MM-dd HH:mm:ss）
            """)
    public ToolResult<ParcelVO> queryParcelByTrackingNumber(
            @P("快递包裹的完整运单号") String trackingNumber) {
        // 参数校验
        if (StrUtil.isEmpty(trackingNumber)) {
            return ToolResult.error("运单号不能为空");
        }
        // 远程调用
        Result<ParcelVO> result = parcelFeignClient.queryByTrackingNumber(trackingNumber);
        if (!result.getStatus()) {
            return ToolResult.error(result.getMessage());
        }
        return ToolResult.success(result.getData());
    }

    @Tool("""
            根据条件分页查询到达用户所属门店的包裹信息，查询结果仅包括到达用户所属门店的包裹，不包括其他门店。
            按情况而定，可能需要多次调用查询，可以增加pageSize的大小以减少查询次数，例如100。
            例如查询近一周的包裹，你需要多次查询，直到分页查询完才结束。
            本工具部分参数为选填，表示不作为数据库查询条件，例如不指定运单号，则查询所有符合其他条件的包裹；
            (本工具返回的data应不为null)
            (推荐用表格展示查询结果)
            返回数据字段说明：
              - id：包裹ID
              - trackingNumber：快递运单号
              - recipientPhone：收件人手机号
              - recipientAddrProvince：收件地址-省
              - recipientAddrCity：收件地址-市
              - recipientAddrDistrict：收件地址-区/县
              - recipientAddrDetail：收件地址-详细地址
              - width：包裹宽度（cm）
              - height：包裹高度（cm）
              - length：包裹长度（cm）
              - weight：包裹重量（kg）
              - storeId：门店ID
              - storeName：门店名称
              - pickCode：取件码（入库后生成）
              - status：包裹状态（0未入库、1已入库、2已取出、3滞留、4退回）
              - inTime：入库时间（yyyy-MM-dd HH:mm:ss）
              - outTime：出库/取件时间（yyyy-MM-dd HH:mm:ss）
            """)
    public ToolResult<PageResult<ParcelVO>> queryParcel(
            @P("(必填) 分页参数：页码") Integer pageNum,
            @P("(必填) 分页参数：每页大小") Integer pageSize,
            @P("(选填) 包裹状态：0=未入库、1=已入库、2=已取出、3=滞留、4=退回；为空表示不指定状态，查询所有") Integer status,
            @P("(选填) 快递包裹的运单号，可片段，非完整；为空表示不指定运单号，查询所有") String trackingNumber,
            @P("(选填) 收件人手机号，可片段，非完整；为空表示不指定收件人手机号，查询所有") String recipientPhone,
            @P("(选填) 要查询的时间段类型：入库时间\"inTime\" 或 出库时间\"outTime\" 或 创建时间\"createTime\"，为空表示不指定查询时间") String timeType,
            @P("(选填) 要查询的时间段的开始时间，格式：\"yyyy-MM-dd\"") LocalDate startTime,
            @P("(选填) 要查询的时间段的结束时间，格式：\"yyyy-MM-dd\"") LocalDate endTime,
            @ToolMemoryId String memoryId) {
        // 参数校验
        if (pageNum == null || pageSize == null) {
            return ToolResult.error("分页参数不能为空");
        }

        try {
            // 从redis中获取用户ID
            Long userId = getUserId(memoryId);

            // 远程调用
            ParcelAdminQueryDTO parcelAdminQueryDTO = new ParcelAdminQueryDTO(
                    pageNum, pageSize,
                    status, trackingNumber, recipientPhone,
                    timeType, startTime, endTime,
                    userId
            );
            Result<PageResult<ParcelVO>> result = parcelFeignClient.adminInfo(parcelAdminQueryDTO);
            // 远程调用异常
            if (!result.getStatus()) {
                return ToolResult.error(result.getMessage());
            }

            return ToolResult.success(result.getData());

        } catch (RuntimeException e) {
            return ToolResult.error(e.getMessage());
        }
    }

    @Tool("""
            分页查询用户所属门店的货架和货架层信息。
            根据获取到的信息，你可以总结每个货架/货架层的使用率等信息。
            按情况而定，可能需要多次调用查询，可以增加pageSize的大小以减少查询次数，例如50。
            (本工具返回的data应不为null)
            (推荐用表格展示查询结果)
            返回数据字段说明：
              - id：货架ID
              - storeId：所属门店ID
              - shelfNo：门店内货架编号
              - layerCount：货架总层数
              - maxWidth：该货架可放包裹最大宽度（cm）
              - maxHeight：该货架可放包裹最大高度（cm）
              - maxLength：该货架可放包裹最大长度（cm）
              - maxWeight：该货架可承受最大重量（kg）
              - status：货架状态（1=正常，0=停用/维护）
              - shelfLayers：货架层列表（每一层的详细信息）
              - shelfLayers[].id：货架层ID
              - shelfLayers[].shelfId：所属货架ID
              - shelfLayers[].layerNo：层号（从1开始）
              - shelfLayers[].todayMaxSeq：当日最大序号（用于取件码生成）
              - shelfLayers[].maxCapacity：该层最大容量（最多可存放包裹数）
              - shelfLayers[].currentCount：该层当前已存放包裹数
            """)
    public ToolResult<PageResult<ShelfAndLayerVO>> queryShelfAndLayer(
            @P("(必填) 分页参数：页码") Integer pageNum,
            @P("(必填) 分页参数：每页大小") Integer pageSize,
            @ToolMemoryId String memoryId) {
        try {
            // 从redis中获取用户ID
            Long userId = getUserId(memoryId);

            // 远程调用
            ShelfQueryDTO shelfQueryDTO = new ShelfQueryDTO(pageNum, pageSize, userId);
            Result<PageResult<ShelfAndLayerVO>> result = storeFeignClient.queryShelfAndLayer(shelfQueryDTO);
            // 远程调用异常
            if (!result.getStatus()) {
                return ToolResult.error(result.getMessage());
            }

            return ToolResult.success(result.getData());

        } catch (RuntimeException e) {
            return ToolResult.error(e.getMessage());
        }
    }

    @Tool("""
            查询用户所属门店信息。
            (本工具返回的data应不为null)
            (推荐用表格展示查询结果)
            返回数据字段说明：
              - id: 门店ID
              - managerUserId
              - managerPhone: 门店管理员手机号
              - storeName: 门店名称
              - addrProvince: 门店地址-省
              - addrCity: 门店地址-市
              - addrDistrict: 门店地址-区
              - addrDetail: 门店地址-详细地址
              - status: 门店状态（1=营业、2=休息、3=注销），不需要将状态编码呈现给用户
            """)
    public ToolResult<StoreVO> getUserStore(@ToolMemoryId String memoryId) {
        try {
            // 从redis中获取用户ID
            Long userId = getUserId(memoryId);

            // 远程调用
            Result<StoreVO> result = storeFeignClient.getUserStore(userId);
            // 远程调用异常
            if (!result.getStatus()) {
                return ToolResult.error(result.getMessage());
            }
            return ToolResult.success(result.getData());

        } catch (RuntimeException e) {
            return ToolResult.error(e.getMessage());
        }
    }

}
