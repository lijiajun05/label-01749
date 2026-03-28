package com.classmanage.service;

import com.classmanage.entity.AttendanceRecord;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface AttendanceService {
    
    Page<AttendanceRecord> getPage(Integer pageNum, Integer pageSize, Long studentId, Long courseId, String date);
    
    List<AttendanceRecord> getByStudentId(Long studentId);
    
    List<AttendanceRecord> getByCourseId(Long courseId);
    
    void create(AttendanceRecord attendance);
    
    void batchCreate(List<AttendanceRecord> attendances);
    
    void update(AttendanceRecord attendance);
    
    void delete(Long id);
    
    Map<String, Object> getStudentStatistics(Long studentId);
    
    Map<String, Object> getClassStatistics(Long classId);
}
