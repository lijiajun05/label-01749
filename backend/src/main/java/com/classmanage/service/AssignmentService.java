package com.classmanage.service;

import com.classmanage.entity.AssignmentInfo;
import com.classmanage.entity.AssignmentSubmit;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface AssignmentService {
    
    Page<AssignmentInfo> getPage(Integer pageNum, Integer pageSize, Long courseId, String title);
    
    AssignmentInfo getById(Long id);
    
    void create(AssignmentInfo assignment);
    
    void update(AssignmentInfo assignment);
    
    void delete(Long id);
    
    List<Map<String, Object>> getSubmits(Long assignmentId);
    
    void recordSubmit(AssignmentSubmit submit);
    
    void updateSubmit(Long submitId, AssignmentSubmit submit);
    
    List<Map<String, Object>> getStudentAssignments(Long studentId);
    
    Map<String, Object> getClassStatistics(Long classId);
}
