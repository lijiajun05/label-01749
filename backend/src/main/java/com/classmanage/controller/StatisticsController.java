package com.classmanage.controller;

import com.classmanage.common.result.Result;
import com.classmanage.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER')")
    public Result<Map<String, Object>> getDashboard(@RequestParam(required = false) Long classId) {
        return Result.success(statisticsService.getDashboardData(classId));
    }
    
    @GetMapping("/attendance/trend")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER')")
    public Result<Map<String, Object>> getAttendanceTrend(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(statisticsService.getAttendanceTrend(classId, startDate, endDate));
    }
    
    @GetMapping("/grade/distribution")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER')")
    public Result<Map<String, Object>> getGradeDistribution(@RequestParam(required = false) Long classId) {
        return Result.success(statisticsService.getGradeDistribution(classId));
    }
    
    @GetMapping("/assignment/completion")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER')")
    public Result<Map<String, Object>> getAssignmentCompletion(@RequestParam(required = false) Long classId) {
        return Result.success(statisticsService.getAssignmentCompletion(classId));
    }
    
    @GetMapping("/study-style/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER')")
    public Result<Map<String, Object>> getClassStudyStyle(@PathVariable Long classId) {
        return Result.success(statisticsService.getClassStudyStyle(classId));
    }
    
    @GetMapping("/study-style/student/{studentId}")
    public Result<Map<String, Object>> getStudentStudyStyle(@PathVariable Long studentId) {
        return Result.success(statisticsService.getStudentStudyStyle(studentId));
    }
}
