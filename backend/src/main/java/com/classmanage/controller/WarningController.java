package com.classmanage.controller;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.result.PageResult;
import com.classmanage.common.result.Result;
import com.classmanage.common.utils.SecurityUtils;
import com.classmanage.entity.WarningRecord;
import com.classmanage.service.WarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warnings")
@RequiredArgsConstructor
public class WarningController {
    
    private final WarningService warningService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER', 'STUDENT')")
    public Result<PageResult<WarningRecord>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer warningType,
            @RequestParam(required = false) Integer status) {
        Page<WarningRecord> page = warningService.getPage(pageNum, pageSize, studentId, warningType, status);
        return Result.success(PageResult.of(page));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER', 'STUDENT')")
    public Result<WarningRecord> getById(@PathVariable Long id) {
        return Result.success(warningService.getById(id));
    }
    
    @GetMapping("/student/{studentId}")
    public Result<List<WarningRecord>> getByStudent(@PathVariable Long studentId) {
        return Result.success(warningService.getByStudentId(studentId));
    }
    
    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    public Result<List<WarningRecord>> getByClass(@PathVariable Long classId) {
        return Result.success(warningService.getByClassId(classId));
    }
    
    @PutMapping("/{id}/handle")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "预警管理", type = "修改", description = "处理预警")
    public Result<Void> handleWarning(@PathVariable Long id, @RequestBody Map<String, String> params) {
        Long handlerId = SecurityUtils.getCurrentUserId();
        String handleResult = params.get("handleResult");
        warningService.handleWarning(id, handlerId, handleResult);
        return Result.success();
    }
    
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "预警管理", type = "新增", description = "手动生成预警")
    public Result<Void> generateWarnings() {
        warningService.generateWarnings();
        return Result.success();
    }
    
    @GetMapping("/statistics/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    public Result<Map<String, Object>> getClassStatistics(@PathVariable Long classId) {
        return Result.success(warningService.getClassStatistics(classId));
    }
}
