package com.classmanage.controller;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.result.Result;
import com.classmanage.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据备份控制器
 */
@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
public class BackupController {
    
    private final BackupService backupService;
    
    /**
     * 手动执行备份
     */
    @PostMapping("/execute")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "数据备份", type = "备份", description = "执行数据库备份")
    public Result<String> executeBackup() {
        String backupFile = backupService.executeBackup();
        return Result.success(backupFile);
    }
    
    /**
     * 获取备份列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Map<String, Object>>> getBackupList() {
        return Result.success(backupService.getBackupList());
    }
    
    /**
     * 删除备份文件
     */
    @DeleteMapping("/{filename}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "数据备份", type = "删除", description = "删除备份文件")
    public Result<Void> deleteBackup(@PathVariable String filename) {
        backupService.deleteBackup(filename);
        return Result.success();
    }
    
    /**
     * 下载备份文件
     */
    @GetMapping("/download/{filename}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadBackup(@PathVariable String filename) {
        byte[] data = backupService.downloadBackup(filename);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(data.length);
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(data);
    }
}
