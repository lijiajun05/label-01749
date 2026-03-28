package com.classmanage.service;

import java.util.List;
import java.util.Map;

/**
 * 数据备份服务接口
 */
public interface BackupService {
    
    /**
     * 执行数据库备份
     * @return 备份文件路径
     */
    String executeBackup();
    
    /**
     * 获取备份文件列表
     */
    List<Map<String, Object>> getBackupList();
    
    /**
     * 删除备份文件
     */
    void deleteBackup(String filename);
    
    /**
     * 下载备份文件
     */
    byte[] downloadBackup(String filename);
    
    /**
     * 清理过期备份
     */
    void cleanExpiredBackups();
}
