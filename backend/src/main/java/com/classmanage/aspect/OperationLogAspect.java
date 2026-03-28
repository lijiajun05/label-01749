package com.classmanage.aspect;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.utils.SecurityUtils;
import com.classmanage.service.OperationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    
    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Around("@annotation(com.classmanage.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;
        
        try {
            result = point.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            try {
                saveLog(point, startTime, exception);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
    }
    
    private void saveLog(ProceedingJoinPoint point, long startTime, Exception exception) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);
        
        if (annotation == null) {
            return;
        }
        
        // 获取请求信息
        HttpServletRequest request = getRequest();
        String ipAddress = request != null ? getIpAddress(request) : "unknown";
        
        // 构建日志内容
        String content = buildContent(annotation, point, exception);
        
        // 保存日志
        com.classmanage.entity.OperationLog logEntity = new com.classmanage.entity.OperationLog();
        logEntity.setUserId(SecurityUtils.getUserId());
        logEntity.setUsername(SecurityUtils.getUsername());
        logEntity.setOperationType(annotation.type());
        logEntity.setModule(annotation.module());
        logEntity.setContent(content);
        logEntity.setIpAddress(ipAddress);
        
        operationLogService.save(logEntity);
    }

    
    private String buildContent(OperationLog annotation, ProceedingJoinPoint point, Exception exception) {
        StringBuilder sb = new StringBuilder();
        sb.append(annotation.description());
        
        if (exception != null) {
            sb.append(" [失败: ").append(exception.getMessage()).append("]");
        }
        
        return sb.toString();
    }
    
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
    
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
