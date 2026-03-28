package com.classmanage.controller;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.result.PageResult;
import com.classmanage.common.result.Result;
import com.classmanage.common.utils.SecurityUtils;
import com.classmanage.entity.NoticeInfo;
import com.classmanage.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {
    
    private final NoticeService noticeService;
    
    @GetMapping
    public Result<PageResult<NoticeInfo>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer noticeType,
            @RequestParam(required = false) Integer status) {
        Page<NoticeInfo> page = noticeService.getPage(pageNum, pageSize, classId, noticeType, status);
        return Result.success(PageResult.of(page));
    }
    
    @GetMapping("/{id}")
    public Result<NoticeInfo> getById(@PathVariable Long id) {
        return Result.success(noticeService.getById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "通知管理", type = "新增", description = "发布通知")
    public Result<Void> create(@RequestBody NoticeInfo notice) {
        notice.setPublisherId(SecurityUtils.getCurrentUserId());
        noticeService.create(notice);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "通知管理", type = "修改", description = "修改通知")
    public Result<Void> update(@PathVariable Long id, @RequestBody NoticeInfo notice) {
        notice.setId(id);
        noticeService.update(notice);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "通知管理", type = "删除", description = "删除通知")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success();
    }
    
    @PutMapping("/{id}/top")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "通知管理", type = "修改", description = "置顶通知")
    public Result<Void> setTop(@PathVariable Long id, @RequestParam boolean isTop) {
        noticeService.setTop(id, isTop);
        return Result.success();
    }
    
    @PutMapping("/{id}/withdraw")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    @OperationLog(module = "通知管理", type = "修改", description = "撤回通知")
    public Result<Void> withdraw(@PathVariable Long id) {
        noticeService.withdraw(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        noticeService.markAsRead(id, userId);
        return Result.success();
    }
    
    @GetMapping("/{id}/read-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    public Result<List<Map<String, Object>>> getReadStatus(@PathVariable Long id) {
        return Result.success(noticeService.getReadStatus(id));
    }
    
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(noticeService.getUnreadCount(userId));
    }
}
