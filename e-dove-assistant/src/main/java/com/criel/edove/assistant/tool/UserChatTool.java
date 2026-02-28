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
import com.criel.edove.feign.user.client.UserFeignClient;
import com.criel.edove.feign.user.vo.UserInfoVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 管理端AI聊天工具
 */
@Component
@RequiredArgsConstructor
public class UserChatTool {

    private final ParcelFeignClient parcelFeignClient;
    private final UserFeignClient userFeignClient;

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

    /**
     * 统一处理远程调用结果
     *
     * @return 成功返回ToolResult.success(data)，失败返回ToolResult.error(message)
     */
    private <T> ToolResult<T> remoteCallAndCheck(Supplier<Result<T>> remoteCall) {
        Result<T> result = remoteCall.get();
        if (!result.getStatus()) {
            return ToolResult.error(result.getMessage());
        }
        return ToolResult.success(result.getData());
    }

    /**
     * 封装：先根据 memoryId 获取 userId，再做远程调用 + 结果校验
     *
     * @param remoteCallWithUserId 远程调用函数，参数仅为一个，即 userId
     */
    private <T> ToolResult<T> remoteCallWithUserAndCheck(
            String memoryId,
            Function<Long, Result<T>> remoteCallWithUserId) {
        try {
            Long userId = getUserId(memoryId);
            return remoteCallAndCheck(() -> remoteCallWithUserId.apply(userId));
        } catch (RuntimeException e) {
            return ToolResult.error(e.getMessage());
        }
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
              - createTime：包裹创建时间
            """)
    public ToolResult<ParcelVO> queryParcelByTrackingNumber(
            @P("快递包裹的完整运单号") String trackingNumber
    ) {
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
            查询当前对话用户的信息。
            (本工具返回的data应不为null)
            (推荐用表格展示查询结果)
            返回数据字段说明：
              - userId: 用户ID
              - storeId: 所属门店ID（工作人员才有值）
              - username: 用户名
              - phone: 用户手机号（必有）
              - email: 用户邮箱（绑定了才有值）
              - avatarUrl: 用户头像URL
            """)
    public ToolResult<UserInfoVO> getUserInfo(@ToolMemoryId String memoryId) {
        return remoteCallWithUserAndCheck(memoryId, userFeignClient::getUserInfo);
    }

}
