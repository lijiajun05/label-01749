package com.classmanage.service.impl;

import com.classmanage.common.exception.BusinessException;
import com.classmanage.entity.GradeInfo;
import com.classmanage.entity.StudentInfo;
import com.classmanage.repository.*;
import com.classmanage.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {
    
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    
    @Override
    public Page<GradeInfo> getPage(Integer pageNum, Integer pageSize, Long studentId, Long courseId, Integer gradeType) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<GradeInfo> page = gradeRepository.findByConditions(studentId, courseId, null, pageRequest);
        page.getContent().forEach(this::fillGradeInfo);
        return page;
    }
    
    @Override
    public List<GradeInfo> getByStudentId(Long studentId) {
        List<GradeInfo> grades = gradeRepository.findByStudentId(studentId);
        grades.forEach(this::fillGradeInfo);
        return grades;
    }
    
    @Override
    public List<GradeInfo> getByCourseId(Long courseId) {
        return gradeRepository.findByCourseId(courseId);
    }
    
    @Override
    public void create(GradeInfo grade) {
        validateScore(grade.getScore());
        grade.setCreateTime(LocalDateTime.now());
        gradeRepository.save(grade);
    }
    
    @Override
    public void batchCreate(List<GradeInfo> grades) {
        for (GradeInfo grade : grades) {
            validateScore(grade.getScore());
            grade.setCreateTime(LocalDateTime.now());
        }
        gradeRepository.saveAll(grades);
    }
    
    @Override
    public void update(GradeInfo grade) {
        GradeInfo existing = gradeRepository.findById(grade.getId())
            .orElseThrow(() -> new BusinessException("成绩记录不存在"));
        validateScore(grade.getScore());
        
        existing.setScore(grade.getScore());
        existing.setGradeType(grade.getGradeType());
        existing.setSemester(grade.getSemester());
        gradeRepository.save(existing);
    }
    
    @Override
    public void delete(Long id) {
        gradeRepository.deleteById(id);
    }
    
    private void validateScore(BigDecimal score) {
        if (score == null) {
            throw new BusinessException("成绩不能为空");
        }
        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessException("成绩必须在0-100之间");
        }
    }

    @Override
    public Map<String, Object> getStudentStatistics(Long studentId) {
        Map<String, Object> result = new HashMap<>();
        List<GradeInfo> grades = getByStudentId(studentId);
        
        if (grades.isEmpty()) {
            result.put("averageScore", 0);
            result.put("totalCourses", 0);
            result.put("passRate", 0);
            return result;
        }
        
        double avgScore = grades.stream()
            .mapToDouble(g -> g.getScore().doubleValue())
            .average()
            .orElse(0);
        
        long passCount = grades.stream()
            .filter(g -> g.getScore().compareTo(new BigDecimal("60")) >= 0)
            .count();
        
        result.put("averageScore", Math.round(avgScore * 100) / 100.0);
        result.put("totalCourses", grades.size());
        result.put("passRate", (double) passCount / grades.size() * 100);
        result.put("failCount", grades.size() - passCount);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getClassStatistics(Long classId) {
        Map<String, Object> result = new HashMap<>();
        
        List<StudentInfo> students = studentRepository.findByClassId(classId);
        if (students.isEmpty()) {
            result.put("studentCount", 0);
            result.put("averageScore", 0);
            return result;
        }
        
        List<Long> studentIds = students.stream().map(StudentInfo::getId).toList();
        List<GradeInfo> grades = gradeRepository.findByStudentIdIn(studentIds);
        
        if (grades.isEmpty()) {
            result.put("studentCount", students.size());
            result.put("averageScore", 0);
            result.put("passRate", 0);
            return result;
        }
        
        double avgScore = grades.stream()
            .mapToDouble(g -> g.getScore().doubleValue())
            .average()
            .orElse(0);
        
        long passCount = grades.stream()
            .filter(g -> g.getScore().compareTo(new BigDecimal("60")) >= 0)
            .count();
        
        long excellentCount = grades.stream()
            .filter(g -> g.getScore().compareTo(new BigDecimal("90")) >= 0)
            .count();
        
        result.put("studentCount", students.size());
        result.put("averageScore", Math.round(avgScore * 100) / 100.0);
        result.put("passRate", (double) passCount / grades.size() * 100);
        result.put("excellentRate", (double) excellentCount / grades.size() * 100);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getCourseDistribution(Long courseId) {
        Map<String, Object> result = new HashMap<>();
        List<GradeInfo> grades = getByCourseId(courseId);
        
        int[] ranges = new int[5];
        for (GradeInfo grade : grades) {
            double score = grade.getScore().doubleValue();
            if (score < 60) ranges[0]++;
            else if (score < 70) ranges[1]++;
            else if (score < 80) ranges[2]++;
            else if (score < 90) ranges[3]++;
            else ranges[4]++;
        }
        
        List<Map<String, Object>> distribution = new ArrayList<>();
        String[] labels = {"不及格(0-59)", "及格(60-69)", "中等(70-79)", "良好(80-89)", "优秀(90-100)"};
        for (int i = 0; i < 5; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", labels[i]);
            item.put("value", ranges[i]);
            distribution.add(item);
        }
        
        result.put("distribution", distribution);
        result.put("total", grades.size());
        
        return result;
    }
    
    private void fillGradeInfo(GradeInfo grade) {
        if (grade.getStudentId() != null) {
            studentRepository.findById(grade.getStudentId()).ifPresent(student -> {
                grade.setStudentNo(student.getStudentNo());
                if (student.getUserId() != null) {
                    userRepository.findById(student.getUserId()).ifPresent(user -> {
                        grade.setStudentName(user.getRealName());
                    });
                }
            });
        }
        if (grade.getCourseId() != null) {
            courseRepository.findById(grade.getCourseId()).ifPresent(course -> {
                grade.setCourseName(course.getCourseName());
            });
        }
    }
}
