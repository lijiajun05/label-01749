package com.classmanage.controller;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.result.PageResult;
import com.classmanage.common.result.Result;
import com.classmanage.entity.ClassMeeting;
import com.classmanage.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MeetingController {
    
    private final MeetingService meetingService;
    
    @GetMapping("/meetings")
    public Result<PageResult<ClassMeeting>> listAll(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ClassMeeting> page = meetingService.getPage(null, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }
    
    @GetMapping("/classes/{classId}/meetings")
    public Result<PageResult<ClassMeeting>> list(
            @PathVariable Long classId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ClassMeeting> page = meetingService.getPage(classId, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }
    
    @GetMapping("/meetings/{id}")
    public Result<ClassMeeting> getById(@PathVariable Long id) {
        return Result.success(meetingService.getById(id));
    }
    
    @PostMapping("/classes/{classId}/meetings")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "班会管理", type = "新增", description = "新增班会记录")
    public Result<Void> create(@PathVariable Long classId, @RequestBody ClassMeeting meeting) {
        meeting.setClassId(classId);
        meetingService.create(meeting);
        return Result.success();
    }
    
    @PutMapping("/meetings/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "班会管理", type = "修改", description = "修改班会记录")
    public Result<Void> update(@PathVariable Long id, @RequestBody ClassMeeting meeting) {
        meeting.setId(id);
        meetingService.update(meeting);
        return Result.success();
    }
    
    @DeleteMapping("/meetings/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "班会管理", type = "删除", description = "删除班会记录")
    public Result<Void> delete(@PathVariable Long id) {
        meetingService.delete(id);
        return Result.success();
    }
}
