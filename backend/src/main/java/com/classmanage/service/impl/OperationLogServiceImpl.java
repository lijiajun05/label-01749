package com.classmanage.service.impl;

import com.classmanage.entity.OperationLog;
import com.classmanage.repository.OperationLogRepository;
import com.classmanage.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 操作日志服务实现
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {
    
    private final OperationLogRepository operationLogRepository;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void save(OperationLog log) {
        operationLogRepository.save(log);
    }
    
    @Override
    public Page<OperationLog> pageList(Integer pageNum, Integer pageSize, String username,
                                        String operationType, String startTime, String endTime) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);
        
        LocalDateTime start = StringUtils.hasText(startTime) ? LocalDateTime.parse(startTime, FORMATTER) : null;
        LocalDateTime end = StringUtils.hasText(endTime) ? LocalDateTime.parse(endTime, FORMATTER) : null;
        
        return operationLogRepository.findByConditions(
            StringUtils.hasText(username) ? username : null,
            StringUtils.hasText(operationType) ? operationType : null,
            start,
            end,
            pageRequest
        );
    }
}
