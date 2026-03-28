package com.classmanage.controller;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.result.PageResult;
import com.classmanage.common.result.Result;
import com.classmanage.entity.AttendanceRecord;
import com.classmanage.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    @GetMapping
    public Result<PageResult<AttendanceRecord>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String date) {
        Page<AttendanceRecord> page = attendanceService.getPage(pageNum, pageSize, studentId, courseId, date);
        return Result.success(PageResult.of(page));
    }
    
    @GetMapping("/student/{studentId}")
    public Result<List<AttendanceRecord>> getByStudent(@PathVariable Long studentId) {
        return Result.success(attendanceService.getByStudentId(studentId));
    }
    
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    public Result<List<AttendanceRecord>> getByCourse(@PathVariable Long courseId) {
        return Result.success(attendanceService.getByCourseId(courseId));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "考勤管理", type = "新增", description = "录入考勤")
    public Result<Void> create(@RequestBody AttendanceRecord attendance) {
        attendanceService.create(attendance);
        return Result.success();
    }
    
    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "考勤管理", type = "新增", description = "批量录入考勤")
    public Result<Void> batchCreate(@RequestBody List<AttendanceRecord> attendances) {
        attendanceService.batchCreate(attendances);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "考勤管理", type = "修改", description = "修改考勤")
    public Result<Void> update(@PathVariable Long id, @RequestBody AttendanceRecord attendance) {
        attendance.setId(id);
        attendanceService.update(attendance);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "考勤管理", type = "删除", description = "删除考勤")
    public Result<Void> delete(@PathVariable Long id) {
        attendanceService.delete(id);
        return Result.success();
    }
    
    @GetMapping("/statistics/student/{studentId}")
    public Result<Map<String, Object>> getStudentStatistics(@PathVariable Long studentId) {
        return Result.success(attendanceService.getStudentStatistics(studentId));
    }
    
    @GetMapping("/statistics/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    public Result<Map<String, Object>> getClassStatistics(@PathVariable Long classId) {
        return Result.success(attendanceService.getClassStatistics(classId));
    }
}
