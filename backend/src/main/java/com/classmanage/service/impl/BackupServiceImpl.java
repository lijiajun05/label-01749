package com.classmanage.service.impl;

import com.classmanage.common.exception.BusinessException;
import com.classmanage.service.BackupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * 数据备份服务实现
 */
@Slf4j
@Service
public class BackupServiceImpl implements BackupService {
    
    @Value("${spring.datasource.url}")
    private String dbUrl;
    
    @Value("${spring.datasource.username}")
    private String dbUsername;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    @Value("${backup.enabled:true}")
    private boolean backupEnabled;
    
    @Value("${backup.path:./backups}")
    private String backupPath;
    
    @Value("${backup.retention-days:30}")
    private int retentionDays;
    
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    @Override
    public String executeBackup() {
        try {
            // 确保备份目录存在
            Path backupDir = Paths.get(backupPath);
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }
            
            // 解析数据库名称
            String dbName = extractDatabaseName(dbUrl);
            String dbHost = extractHost(dbUrl);
            String dbPort = extractPort(dbUrl);
            
            // 生成备份文件名
            String timestamp = LocalDateTime.now().format(FILE_DATE_FORMAT);
            String filename = String.format("backup_%s_%s.sql", dbName, timestamp);
            Path backupFile = backupDir.resolve(filename);
            
            // 执行mysqldump命令
            ProcessBuilder pb = new ProcessBuilder(
                "mysqldump",
                "-h", dbHost,
                "-P", dbPort,
                "-u", dbUsername,
                "-p" + dbPassword,
                "--single-transaction",
                "--routines",
                "--triggers",
                "--databases", dbName
            );
            
            pb.redirectOutput(backupFile.toFile());
            pb.redirectErrorStream(false);
            
            Process process = pb.start();
            
            // 读取错误输出
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 忽略警告信息
                    if (!line.contains("Warning")) {
                        errorOutput.append(line).append("\n");
                    }
                }
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                Files.deleteIfExists(backupFile);
                throw new BusinessException("数据库备份失败: " + errorOutput);
            }
            
            // 验证备份文件
            if (!Files.exists(backupFile) || Files.size(backupFile) == 0) {
                Files.deleteIfExists(backupFile);
                throw new BusinessException("备份文件创建失败或为空");
            }
            
            log.info("数据库备份成功: {}", backupFile);
            return backupFile.toString();
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("数据库备份失败", e);
            throw new BusinessException("数据库备份失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Map<String, Object>> getBackupList() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            Path backupDir = Paths.get(backupPath);
            if (!Files.exists(backupDir)) {
                return result;
            }
            
            try (Stream<Path> files = Files.list(backupDir)) {
                files.filter(p -> p.toString().endsWith(".sql"))
                     .sorted(Comparator.comparing(Path::getFileName).reversed())
                     .forEach(file -> {
                         try {
                             Map<String, Object> info = new HashMap<>();
                             info.put("filename", file.getFileName().toString());
                             info.put("size", Files.size(file));
                             info.put("sizeFormatted", formatFileSize(Files.size(file)));
                             info.put("createTime", Files.getLastModifiedTime(file).toInstant().toString());
                             result.add(info);
                         } catch (IOException e) {
                             log.warn("读取备份文件信息失败: {}", file, e);
                         }
                     });
            }
        } catch (IOException e) {
            log.error("获取备份列表失败", e);
        }
        
        return result;
    }
    
    @Override
    public void deleteBackup(String filename) {
        try {
            // 安全检查：防止路径遍历攻击
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                throw new BusinessException("非法的文件名");
            }
            
            Path backupFile = Paths.get(backupPath, filename);
            if (!Files.exists(backupFile)) {
                throw new BusinessException("备份文件不存在");
            }
            
            Files.delete(backupFile);
            log.info("删除备份文件: {}", filename);
            
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("删除备份文件失败", e);
            throw new BusinessException("删除备份文件失败: " + e.getMessage());
        }
    }
    
    @Override
    public byte[] downloadBackup(String filename) {
        try {
            // 安全检查
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                throw new BusinessException("非法的文件名");
            }
            
            Path backupFile = Paths.get(backupPath, filename);
            if (!Files.exists(backupFile)) {
                throw new BusinessException("备份文件不存在");
            }
            
            return Files.readAllBytes(backupFile);
            
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("读取备份文件失败", e);
            throw new BusinessException("读取备份文件失败: " + e.getMessage());
        }
    }
    
    @Override
    @Scheduled(cron = "${backup.cron:0 0 2 * * ?}")
    public void cleanExpiredBackups() {
        if (!backupEnabled) {
            return;
        }
        
        log.info("开始清理过期备份文件...");
        
        try {
            Path backupDir = Paths.get(backupPath);
            if (!Files.exists(backupDir)) {
                return;
            }
            
            LocalDate cutoffDate = LocalDate.now().minusDays(retentionDays);
            
            try (Stream<Path> files = Files.list(backupDir)) {
                files.filter(p -> p.toString().endsWith(".sql"))
                     .forEach(file -> {
                         try {
                             LocalDate fileDate = LocalDate.from(
                                 Files.getLastModifiedTime(file).toInstant()
                                     .atZone(java.time.ZoneId.systemDefault())
                             );
                             
                             if (fileDate.isBefore(cutoffDate)) {
                                 Files.delete(file);
                                 log.info("删除过期备份: {}", file.getFileName());
                             }
                         } catch (IOException e) {
                             log.warn("处理备份文件失败: {}", file, e);
                         }
                     });
            }
            
            log.info("过期备份清理完成");
            
        } catch (IOException e) {
            log.error("清理过期备份失败", e);
        }
    }
    
    /**
     * 定时执行数据库备份
     */
    @Scheduled(cron = "${backup.cron:0 0 2 * * ?}")
    public void scheduledBackup() {
        if (!backupEnabled) {
            log.info("数据备份功能已禁用");
            return;
        }
        
        log.info("开始执行定时数据库备份...");
        try {
            String backupFile = executeBackup();
            log.info("定时备份完成: {}", backupFile);
        } catch (Exception e) {
            log.error("定时备份失败", e);
        }
    }
    
    private String extractDatabaseName(String url) {
        // jdbc:mysql://localhost:3306/class_manage?...
        String path = url.substring(url.lastIndexOf("/") + 1);
        int queryIndex = path.indexOf("?");
        return queryIndex > 0 ? path.substring(0, queryIndex) : path;
    }
    
    private String extractHost(String url) {
        // jdbc:mysql://localhost:3306/class_manage
        String withoutProtocol = url.substring(url.indexOf("//") + 2);
        int colonIndex = withoutProtocol.indexOf(":");
        int slashIndex = withoutProtocol.indexOf("/");
        if (colonIndex > 0 && colonIndex < slashIndex) {
            return withoutProtocol.substring(0, colonIndex);
        }
        return withoutProtocol.substring(0, slashIndex);
    }
    
    private String extractPort(String url) {
        String withoutProtocol = url.substring(url.indexOf("//") + 2);
        int colonIndex = withoutProtocol.indexOf(":");
        int slashIndex = withoutProtocol.indexOf("/");
        if (colonIndex > 0 && colonIndex < slashIndex) {
            return withoutProtocol.substring(colonIndex + 1, slashIndex);
        }
        return "3306";
    }
    
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
