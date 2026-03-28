package com.classmanage.service;

import com.classmanage.entity.GradeInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface GradeService {
    
    Page<GradeInfo> getPage(Integer pageNum, Integer pageSize, Long studentId, Long courseId, Integer gradeType);
    
    List<GradeInfo> getByStudentId(Long studentId);
    
    List<GradeInfo> getByCourseId(Long courseId);
    
    void create(GradeInfo grade);
    
    void batchCreate(List<GradeInfo> grades);
    
    void update(GradeInfo grade);
    
    void delete(Long id);
    
    Map<String, Object> getStudentStatistics(Long studentId);
    
    Map<String, Object> getClassStatistics(Long classId);
    
    Map<String, Object> getCourseDistribution(Long courseId);
}
