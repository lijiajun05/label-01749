package com.classmanage.controller;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.result.PageResult;
import com.classmanage.common.result.Result;
import com.classmanage.entity.AssignmentInfo;
import com.classmanage.entity.AssignmentSubmit;
import com.classmanage.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    
    private final AssignmentService assignmentService;
    
    @GetMapping
    public Result<PageResult<AssignmentInfo>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String title) {
        Page<AssignmentInfo> page = assignmentService.getPage(pageNum, pageSize, courseId, title);
        return Result.success(PageResult.of(page));
    }
    
    @GetMapping("/{id}")
    public Result<AssignmentInfo> getById(@PathVariable Long id) {
        return Result.success(assignmentService.getById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "作业管理", type = "新增", description = "发布作业")
    public Result<Void> create(@RequestBody AssignmentInfo assignment) {
        assignmentService.create(assignment);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "作业管理", type = "修改", description = "修改作业")
    public Result<Void> update(@PathVariable Long id, @RequestBody AssignmentInfo assignment) {
        assignment.setId(id);
        assignmentService.update(assignment);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "作业管理", type = "删除", description = "删除作业")
    public Result<Void> delete(@PathVariable Long id) {
        assignmentService.delete(id);
        return Result.success();
    }
    
    @GetMapping("/{id}/submits")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    public Result<List<Map<String, Object>>> getSubmits(@PathVariable Long id) {
        return Result.success(assignmentService.getSubmits(id));
    }
    
    @PostMapping("/{id}/submits")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "作业管理", type = "新增", description = "记录作业提交")
    public Result<Void> recordSubmit(@PathVariable Long id, @RequestBody AssignmentSubmit submit) {
        submit.setAssignmentId(id);
        assignmentService.recordSubmit(submit);
        return Result.success();
    }
    
    @PutMapping("/submits/{submitId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "作业管理", type = "修改", description = "更新提交状态")
    public Result<Void> updateSubmit(@PathVariable Long submitId, @RequestBody AssignmentSubmit submit) {
        assignmentService.updateSubmit(submitId, submit);
        return Result.success();
    }
    
    @GetMapping("/student/{studentId}")
    public Result<List<Map<String, Object>>> getStudentAssignments(@PathVariable Long studentId) {
        return Result.success(assignmentService.getStudentAssignments(studentId));
    }
    
    @GetMapping("/statistics/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    public Result<Map<String, Object>> getClassStatistics(@PathVariable Long classId) {
        return Result.success(assignmentService.getClassStatistics(classId));
    }
}
