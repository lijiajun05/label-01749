package com.classmanage.controller;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.result.PageResult;
import com.classmanage.common.result.Result;
import com.classmanage.common.utils.SecurityUtils;
import com.classmanage.entity.StudentInfo;
import com.classmanage.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService studentService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Long classId) {
        Page<Map<String, Object>> page = studentService.getPage(pageNum, pageSize, studentNo, realName, classId);
        return Result.success(PageResult.of(page));
    }
    
    @GetMapping("/current")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Map<String, Object>> getCurrentStudent() {
        Long userId = SecurityUtils.getUserId();
        Map<String, Object> student = studentService.getByUserId(userId);
        return Result.success(student);
    }
    
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(studentService.getById(id));
    }
    
    @GetMapping("/all")
    public Result<java.util.List<Map<String, Object>>> getAll() {
        return Result.success(studentService.getAll());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "学生管理", type = "新增", description = "新增学生")
    public Result<Void> create(@RequestBody Map<String, Object> params) {
        StudentInfo student = new StudentInfo();
        student.setStudentNo((String) params.get("studentNo"));
        student.setClassId(Long.valueOf(params.get("classId").toString()));
        student.setGender((String) params.get("gender"));
        
        String realName = (String) params.get("realName");
        String phone = (String) params.get("phone");
        String email = (String) params.get("email");
        
        studentService.create(student, realName, phone, email);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "学生管理", type = "修改", description = "修改学生")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        StudentInfo student = new StudentInfo();
        student.setStudentNo((String) params.get("studentNo"));
        student.setClassId(Long.valueOf(params.get("classId").toString()));
        student.setGender((String) params.get("gender"));
        
        String realName = (String) params.get("realName");
        String phone = (String) params.get("phone");
        String email = (String) params.get("email");
        
        studentService.update(id, student, realName, phone, email);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "学生管理", type = "删除", description = "删除学生")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Result.success();
    }
}
