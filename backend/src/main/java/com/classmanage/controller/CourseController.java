package com.classmanage.controller;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.result.PageResult;
import com.classmanage.common.result.Result;
import com.classmanage.entity.CourseInfo;
import com.classmanage.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    
    private final CourseService courseService;
    
    @GetMapping
    public Result<PageResult<CourseInfo>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String semester) {
        Page<CourseInfo> page = courseService.getPage(pageNum, pageSize, courseName, teacherId, semester);
        return Result.success(PageResult.of(page));
    }
    
    @GetMapping("/all")
    public Result<List<CourseInfo>> getAll() {
        return Result.success(courseService.getAll());
    }
    
    @GetMapping("/{id}")
    public Result<CourseInfo> getById(@PathVariable Long id) {
        return Result.success(courseService.getById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "课程管理", type = "新增", description = "新增课程")
    public Result<Void> create(@RequestBody CourseInfo course) {
        courseService.create(course);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COURSE_TEACHER')")
    @OperationLog(module = "课程管理", type = "修改", description = "修改课程")
    public Result<Void> update(@PathVariable Long id, @RequestBody CourseInfo course) {
        course.setId(id);
        courseService.update(course);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "课程管理", type = "删除", description = "删除课程")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return Result.success();
    }
}
