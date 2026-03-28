package com.classmanage.service;

import com.classmanage.entity.StudentInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface StudentService {
    
    Page<Map<String, Object>> getPage(Integer pageNum, Integer pageSize, String studentNo, String realName, Long classId);
    
    List<Map<String, Object>> getAll();
    
    Map<String, Object> getById(Long id);
    
    Map<String, Object> getByUserId(Long userId);
    
    void create(StudentInfo student, String realName, String phone, String email);
    
    void update(Long id, StudentInfo student, String realName, String phone, String email);
    
    void delete(Long id);
}
