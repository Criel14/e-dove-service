package com.criel.edove.common.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * 日志切面类：打印请求和响应信息
 */
@Aspect
@Component
public class LogAspect {

    private final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    // 存储每个线程的开始时间
    private static final ThreadLocal<Instant> startTimeThreadLocal = new ThreadLocal<>();

    /**
     * 切点：所有 Controller 公共方法
     */
    @Pointcut("execution(public * com.criel.edove..*Controller.*(..))")
    public void controllerMethods() {
    }

    /**
     * Before: 在目标方法执行前打印请求信息
     */
    @Before("controllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        // 获取请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            LOGGER.warn("无法获取请求上下文");
            return;
        }
        HttpServletRequest req = attributes.getRequest();

        // 记录启始时刻到 ThreadLocal
        startTimeThreadLocal.set(Instant.now());

        // 收集入参信息
        Map<String, Object> reqData = new LinkedHashMap<>();
        reqData.put("URL路径", req.getRequestURI());
        reqData.put("请求方法", req.getMethod());
        String queryString = req.getQueryString();
        if (StrUtil.isEmpty(queryString)) {
            reqData.put("请求参数", queryString);
        }
        reqData.put("IP地址", req.getRemoteAddr());

        // 打印日志
        Signature signature = joinPoint.getSignature();
        LOGGER.info("类名方法: {} : {}", signature.getDeclaringType(), signature.getName());
        try {
            LOGGER.info("请求数据 → {}", JSONUtil.toJsonStr(reqData));
        } catch (Exception e) {
            LOGGER.error("序列化请求日志失败", e);
        }
    }

    /**
     * AfterReturning: 在目标方法成功返回后打印响应信息
     * returning 属性指定方法参数名，用于接收返回值
     */
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(Object result) {
        // 从 ThreadLocal 获取开始时间
        Instant start = startTimeThreadLocal.get();
        if (start == null) {
            LOGGER.warn("日志拦截：无法获取方法开始时间");
            return;
        }

        // 清理 ThreadLocal，防止内存泄漏
        startTimeThreadLocal.remove();

        // 录返回值 + 耗时
        Map<String, Object> respData = new LinkedHashMap<>();
        respData.put("返回结果", JSONUtil.toJsonStr(result));
        respData.put("响应时间", Duration.between(start, Instant.now()).toMillis() + "ms");

        try {
            LOGGER.info("返回数据 ← {}", JSONUtil.toJsonStr(respData));
        } catch (Exception e) {
            LOGGER.error("序列化响应日志失败", e);
        }
    }
}

