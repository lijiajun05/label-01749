package com.classmanage.service;

import com.classmanage.entity.CourseInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CourseService {
    
    Page<CourseInfo> getPage(Integer pageNum, Integer pageSize, String courseName, Long teacherId, String semester);
    
    List<CourseInfo> getAll();
    
    CourseInfo getById(Long id);
    
    void create(CourseInfo course);
    
    void update(CourseInfo course);
    
    void delete(Long id);
}
