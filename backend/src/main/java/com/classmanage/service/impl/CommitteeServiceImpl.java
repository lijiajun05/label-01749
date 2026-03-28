package com.classmanage.service.impl;

import com.classmanage.common.exception.BusinessException;
import com.classmanage.entity.ClassCommittee;
import com.classmanage.repository.ClassRepository;
import com.classmanage.repository.CommitteeRepository;
import com.classmanage.repository.StudentRepository;
import com.classmanage.repository.UserRepository;
import com.classmanage.service.CommitteeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommitteeServiceImpl implements CommitteeService {
    
    private final CommitteeRepository committeeRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    
    @Override
    public List<Map<String, Object>> getByClassId(Long classId) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<ClassCommittee> committees = committeeRepository.findByClassIdAndStatusOrderByPosition(classId, 1);
        
        for (ClassCommittee committee : committees) {
            result.add(buildCommitteeMap(committee));
        }
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<ClassCommittee> committees = committeeRepository.findByStatusOrderByClassIdAscPositionAsc(1);
        
        for (ClassCommittee committee : committees) {
            result.add(buildCommitteeMap(committee));
        }
        return result;
    }
    
    private Map<String, Object> buildCommitteeMap(ClassCommittee committee) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", committee.getId());
        map.put("classId", committee.getClassId());
        map.put("studentId", committee.getStudentId());
        map.put("position", committee.getPosition());
        map.put("startDate", committee.getStartDate());
        map.put("endDate", committee.getEndDate());
        
        classRepository.findById(committee.getClassId()).ifPresent(classInfo -> {
            map.put("className", classInfo.getClassName());
        });
        
        studentRepository.findById(committee.getStudentId()).ifPresent(student -> {
            map.put("studentNo", student.getStudentNo());
            if (student.getUserId() != null) {
                userRepository.findById(student.getUserId()).ifPresent(user -> {
                    map.put("studentName", user.getRealName());
                    map.put("phone", user.getPhone());
                });
            }
        });
        return map;
    }
    
    @Override
    public void create(ClassCommittee committee) {
        committee.setStatus(1);
        committeeRepository.save(committee);
    }
    
    @Override
    public void update(ClassCommittee committee) {
        ClassCommittee existing = committeeRepository.findById(committee.getId())
            .orElseThrow(() -> new BusinessException("班委记录不存在"));
        
        existing.setStudentId(committee.getStudentId());
        existing.setPosition(committee.getPosition());
        existing.setStartDate(committee.getStartDate());
        existing.setEndDate(committee.getEndDate());
        existing.setStatus(committee.getStatus());
        committeeRepository.save(existing);
    }
    
    @Override
    public void delete(Long id) {
        committeeRepository.deleteById(id);
    }
}
