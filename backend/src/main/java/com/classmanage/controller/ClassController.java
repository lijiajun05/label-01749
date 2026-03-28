package com.classmanage.controller;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.result.PageResult;
import com.classmanage.common.result.Result;
import com.classmanage.entity.ClassInfo;
import com.classmanage.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {
    
    private final ClassService classService;
    
    @GetMapping
    public Result<PageResult<ClassInfo>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) Long teacherId) {
        Page<ClassInfo> page = classService.getPage(pageNum, pageSize, className, grade, teacherId);
        return Result.success(PageResult.of(page));
    }
    
    @GetMapping("/all")
    public Result<List<ClassInfo>> getAll() {
        return Result.success(classService.getAll());
    }
    
    @GetMapping("/{id}")
    public Result<ClassInfo> getById(@PathVariable Long id) {
        return Result.success(classService.getById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "班级管理", type = "新增", description = "新增班级")
    public Result<Void> create(@RequestBody ClassInfo classInfo) {
        classService.create(classInfo);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "班级管理", type = "修改", description = "修改班级")
    public Result<Void> update(@PathVariable Long id, @RequestBody ClassInfo classInfo) {
        classInfo.setId(id);
        classService.update(classInfo);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "班级管理", type = "删除", description = "删除班级")
    public Result<Void> delete(@PathVariable Long id) {
        classService.delete(id);
        return Result.success();
    }
    
    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER', 'COURSE_TEACHER')")
    public Result<List<Map<String, Object>>> getStudents(@PathVariable Long id) {
        return Result.success(classService.getStudentList(id));
    }
    
    @GetMapping("/{id}/contacts")
    public Result<List<Map<String, Object>>> getContacts(@PathVariable Long id) {
        return Result.success(classService.getContacts(id));
    }
}
