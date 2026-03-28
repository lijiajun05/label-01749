package com.classmanage.controller;

import com.classmanage.common.result.PageResult;
import com.classmanage.common.result.Result;
import com.classmanage.entity.OperationLog;
import com.classmanage.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {
    
    private final OperationLogService operationLogService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<OperationLog>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        String startTime = StringUtils.hasText(startDate) ? startDate + " 00:00:00" : null;
        String endTime = StringUtils.hasText(endDate) ? endDate + " 23:59:59" : null;
        
        Page<OperationLog> page = operationLogService.pageList(pageNum, pageSize, username, operationType, startTime, endTime);
        return Result.success(PageResult.of(page));
    }
}
