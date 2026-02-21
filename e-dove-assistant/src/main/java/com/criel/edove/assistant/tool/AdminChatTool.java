package com.criel.edove.assistant.tool;

import cn.hutool.core.util.StrUtil;
import com.criel.edove.common.result.Result;
import com.criel.edove.common.result.ToolResult;
import com.criel.edove.feign.parcel.client.ParcelFeignClient;
import com.criel.edove.feign.parcel.vo.ParcelVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 管理端AI聊天工具
 */
@Component
@RequiredArgsConstructor
public class AdminChatTool {

    private final ParcelFeignClient parcelFeignClient;

    // TODO 系统提示词告知大模型ToolResult的结构，若失败怎么处理，是业务失败，还是有什么异常，data为null比是否表示成功等等

    @Tool("根据运单号查询包裹信息。注：本工具返回的data应不为null，否则表示出错")
    public ToolResult<ParcelVO> queryParcelByTrackingNumber(@P("快递包裹的完整运单号") String trackingNumber){
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

}
