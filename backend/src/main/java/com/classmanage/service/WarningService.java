package com.classmanage.service;

import com.classmanage.entity.WarningRecord;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface WarningService {
    
    Page<WarningRecord> getPage(Integer pageNum, Integer pageSize, Long studentId, Integer warningType, Integer status);
    
    WarningRecord getById(Long id);
    
    List<WarningRecord> getByStudentId(Long studentId);
    
    List<WarningRecord> getByClassId(Long classId);
    
    void handleWarning(Long id, Long handlerId, String handleResult);
    
    void generateWarnings();
    
    Map<String, Object> getClassStatistics(Long classId);
}
