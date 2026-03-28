package com.classmanage.controller;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.result.Result;
import com.classmanage.entity.ClassCommittee;
import com.classmanage.service.CommitteeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommitteeController {
    
    private final CommitteeService committeeService;
    
    @GetMapping("/committees")
    public Result<List<Map<String, Object>>> listAll() {
        return Result.success(committeeService.getAll());
    }
    
    @GetMapping("/classes/{classId}/committees")
    public Result<List<Map<String, Object>>> list(@PathVariable Long classId) {
        return Result.success(committeeService.getByClassId(classId));
    }
    
    @PostMapping("/classes/{classId}/committees")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "班委管理", type = "新增", description = "新增班委")
    public Result<Void> create(@PathVariable Long classId, @RequestBody ClassCommittee committee) {
        committee.setClassId(classId);
        committeeService.create(committee);
        return Result.success();
    }
    
    @PutMapping("/committees/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "班委管理", type = "修改", description = "修改班委")
    public Result<Void> update(@PathVariable Long id, @RequestBody ClassCommittee committee) {
        committee.setId(id);
        committeeService.update(committee);
        return Result.success();
    }
    
    @DeleteMapping("/committees/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "班委管理", type = "删除", description = "删除班委")
    public Result<Void> delete(@PathVariable Long id) {
        committeeService.delete(id);
        return Result.success();
    }
}
