package com.classmanage.controller;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.result.PageResult;
import com.classmanage.common.result.Result;
import com.classmanage.entity.GradeInfo;
import com.classmanage.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {
    
    private final GradeService gradeService;
    
    @GetMapping
    public Result<PageResult<GradeInfo>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer gradeType) {
        Page<GradeInfo> page = gradeService.getPage(pageNum, pageSize, studentId, courseId, gradeType);
        return Result.success(PageResult.of(page));
    }
    
    @GetMapping("/student/{studentId}")
    public Result<List<GradeInfo>> getByStudent(@PathVariable Long studentId) {
        return Result.success(gradeService.getByStudentId(studentId));
    }
    
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    public Result<List<GradeInfo>> getByCourse(@PathVariable Long courseId) {
        return Result.success(gradeService.getByCourseId(courseId));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "成绩管理", type = "新增", description = "录入成绩")
    public Result<Void> create(@RequestBody GradeInfo grade) {
        gradeService.create(grade);
        return Result.success();
    }
    
    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "成绩管理", type = "新增", description = "批量录入成绩")
    public Result<Void> batchCreate(@RequestBody List<GradeInfo> grades) {
        gradeService.batchCreate(grades);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "成绩管理", type = "修改", description = "修改成绩")
    public Result<Void> update(@PathVariable Long id, @RequestBody GradeInfo grade) {
        grade.setId(id);
        gradeService.update(grade);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "成绩管理", type = "删除", description = "删除成绩")
    public Result<Void> delete(@PathVariable Long id) {
        gradeService.delete(id);
        return Result.success();
    }
    
    @GetMapping("/statistics/student/{studentId}")
    public Result<Map<String, Object>> getStudentStatistics(@PathVariable Long studentId) {
        return Result.success(gradeService.getStudentStatistics(studentId));
    }
    
    @GetMapping("/statistics/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    public Result<Map<String, Object>> getClassStatistics(@PathVariable Long classId) {
        return Result.success(gradeService.getClassStatistics(classId));
    }
    
    @GetMapping("/distribution/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    public Result<Map<String, Object>> getCourseDistribution(@PathVariable Long courseId) {
        return Result.success(gradeService.getCourseDistribution(courseId));
    }
}
