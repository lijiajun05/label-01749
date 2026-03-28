package com.classmanage.service;

import com.classmanage.entity.ClassInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ClassService {
    
    Page<ClassInfo> getPage(Integer pageNum, Integer pageSize, String className, String grade, Long teacherId);
    
    List<ClassInfo> getAll();
    
    ClassInfo getById(Long id);
    
    void create(ClassInfo classInfo);
    
    void update(ClassInfo classInfo);
    
    void delete(Long id);
    
    List<Map<String, Object>> getStudentList(Long classId);
    
    List<Map<String, Object>> getContacts(Long classId);
}
