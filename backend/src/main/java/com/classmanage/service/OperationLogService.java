package com.classmanage.service;

import com.classmanage.entity.OperationLog;
import org.springframework.data.domain.Page;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {
    
    /**
     * 保存操作日志
     */
    void save(OperationLog log);
    
    /**
     * 分页查询操作日志
     */
    Page<OperationLog> pageList(Integer pageNum, Integer pageSize, String username, 
                                 String operationType, String startTime, String endTime);
}
