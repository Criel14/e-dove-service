package com.criel.edove.assistant.tool;

import cn.hutool.core.util.StrUtil;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.result.PageResult;
import com.criel.edove.common.result.Result;
import com.criel.edove.common.result.ToolResult;
import com.criel.edove.feign.parcel.client.ParcelFeignClient;
import com.criel.edove.feign.parcel.dto.ParcelAdminQueryDTO;
import com.criel.edove.feign.parcel.vo.ParcelVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端AI聊天工具
 */
@Component
@RequiredArgsConstructor
public class AdminChatTool {

    private final ParcelFeignClient parcelFeignClient;
    private final RedissonClient redissonClient;

    @Tool("获取当前时间信息。(本工具返回的data应不为null)")
    public ToolResult<LocalDateTime> queryCurrentTime() {
        return ToolResult.success(LocalDateTime.now());
    }

    @Tool("""
            根据运单号查询包裹信息；支持在所有包裹中查询。
            (本工具返回的data应不为null)
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
            根据条件分页查询所属门店的包裹信息，查询结果仅包括到达用户所属门店的包裹。
            按情况而定，可能需要多次调用查询，或者可以增加pageSize的大小以减少查询次数，例如100。
            例如查询近一周的包裹，你需要多次查询，直到分页查询完才结束。
            本工具部分参数为选填，表示不作为数据库查询条件，例如不指定运单号，则查询所有符合其他条件的包裹；
            (本工具返回的data应不为null)
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

        // 从redis中获取用户ID
        String key = RedisKeyConstant.AI_CHAT_USER_ID + memoryId;
        RBucket<Long> rBucket = redissonClient.getBucket(key);
        if (!rBucket.isExists()) {
            // 获取用户ID失败
           throw new RuntimeException("系统异常");
        }
        Long userId = rBucket.get();

        // 远程调用
        ParcelAdminQueryDTO parcelAdminQueryDTO = new ParcelAdminQueryDTO(
                pageNum, pageSize,
                status, trackingNumber, recipientPhone,
                timeType, startTime, endTime,
                userId
        );
        Result<PageResult<ParcelVO>> result = parcelFeignClient.adminInfo(parcelAdminQueryDTO);
        if (!result.getStatus()) {
            return ToolResult.error(result.getMessage());
        }
        return ToolResult.success(result.getData());
    }

}
