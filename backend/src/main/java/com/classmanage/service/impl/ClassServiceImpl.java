package com.classmanage.service.impl;

import com.classmanage.common.exception.BusinessException;
import com.classmanage.entity.ClassInfo;
import com.classmanage.entity.StudentInfo;
import com.classmanage.entity.User;
import com.classmanage.repository.ClassRepository;
import com.classmanage.repository.StudentRepository;
import com.classmanage.repository.UserRepository;
import com.classmanage.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {
    
    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    
    @Override
    public Page<ClassInfo> getPage(Integer pageNum, Integer pageSize, String className, String grade, Long teacherId) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        return classRepository.findByConditions(
            StringUtils.hasText(className) ? className : null,
            StringUtils.hasText(grade) ? grade : null,
            teacherId,
            pageRequest
        );
    }
    
    @Override
    public List<ClassInfo> getAll() {
        return classRepository.findByStatus(1);
    }
    
    @Override
    public ClassInfo getById(Long id) {
        return classRepository.findById(id)
            .orElseThrow(() -> new BusinessException("班级不存在"));
    }
    
    @Override
    public void create(ClassInfo classInfo) {
        classInfo.setStatus(1);
        classInfo.setStudentCount(0);
        classInfo.setCreateTime(LocalDateTime.now());
        classInfo.setUpdateTime(LocalDateTime.now());
        classRepository.save(classInfo);
    }

    @Override
    public void update(ClassInfo classInfo) {
        ClassInfo existing = classRepository.findById(classInfo.getId())
            .orElseThrow(() -> new BusinessException("班级不存在"));
        
        existing.setClassName(classInfo.getClassName());
        existing.setGrade(classInfo.getGrade());
        existing.setMajor(classInfo.getMajor());
        existing.setDepartment(classInfo.getDepartment());
        existing.setTeacherId(classInfo.getTeacherId());
        existing.setStatus(classInfo.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        classRepository.save(existing);
    }
    
    @Override
    public void delete(Long id) {
        if (!classRepository.existsById(id)) {
            throw new BusinessException("班级不存在");
        }
        
        if (studentRepository.countByClassId(id) > 0) {
            throw new BusinessException("该班级下存在学生，无法删除");
        }
        classRepository.deleteById(id);
    }
    
    @Override
    public List<Map<String, Object>> getStudentList(Long classId) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<StudentInfo> students = studentRepository.findByClassId(classId);
        
        for (StudentInfo student : students) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", student.getId());
            map.put("studentNo", student.getStudentNo());
            map.put("gender", student.getGender());
            
            if (student.getUserId() != null) {
                userRepository.findById(student.getUserId()).ifPresent(user -> {
                    map.put("realName", user.getRealName());
                    map.put("phone", user.getPhone());
                    map.put("email", user.getEmail());
                });
            }
            result.add(map);
        }
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getContacts(Long classId) {
        return getStudentList(classId);
    }
}
