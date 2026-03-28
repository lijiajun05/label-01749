package com.classmanage.service.impl;

import com.classmanage.common.exception.BusinessException;
import com.classmanage.entity.CourseInfo;
import com.classmanage.repository.CourseRepository;
import com.classmanage.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    
    private final CourseRepository courseRepository;
    
    @Override
    public Page<CourseInfo> getPage(Integer pageNum, Integer pageSize, String courseName, Long teacherId, String semester) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        return courseRepository.findByConditions(
            StringUtils.hasText(courseName) ? courseName : null,
            teacherId,
            StringUtils.hasText(semester) ? semester : null,
            pageRequest
        );
    }
    
    @Override
    public List<CourseInfo> getAll() {
        return courseRepository.findByStatus(1);
    }
    
    @Override
    public CourseInfo getById(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new BusinessException("课程不存在"));
    }
    
    @Override
    public void create(CourseInfo course) {
        if (StringUtils.hasText(course.getCourseCode()) 
            && courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new BusinessException("课程代码已存在");
        }
        course.setStatus(1);
        course.setCreateTime(LocalDateTime.now());
        courseRepository.save(course);
    }
    
    @Override
    public void update(CourseInfo course) {
        CourseInfo existing = courseRepository.findById(course.getId())
            .orElseThrow(() -> new BusinessException("课程不存在"));
        
        existing.setCourseName(course.getCourseName());
        existing.setCourseCode(course.getCourseCode());
        existing.setTeacherId(course.getTeacherId());
        existing.setCredit(course.getCredit());
        existing.setSemester(course.getSemester());
        existing.setStatus(course.getStatus());
        courseRepository.save(existing);
    }
    
    @Override
    public void delete(Long id) {
        courseRepository.deleteById(id);
    }
}
