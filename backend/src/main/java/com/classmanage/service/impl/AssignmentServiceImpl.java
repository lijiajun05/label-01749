package com.classmanage.service.impl;

import com.classmanage.common.enums.SubmitStatus;
import com.classmanage.common.exception.BusinessException;
import com.classmanage.entity.*;
import com.classmanage.repository.*;
import com.classmanage.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {
    
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmitRepository submitRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    
    @Override
    public Page<AssignmentInfo> getPage(Integer pageNum, Integer pageSize, Long courseId, String title) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<AssignmentInfo> page = assignmentRepository.findByConditions(
            courseId,
            StringUtils.hasText(title) ? title : null,
            pageRequest
        );
        page.getContent().forEach(this::fillAssignmentInfo);
        return page;
    }
    
    @Override
    public AssignmentInfo getById(Long id) {
        return assignmentRepository.findById(id)
            .orElseThrow(() -> new BusinessException("作业不存在"));
    }
    
    @Override
    public void create(AssignmentInfo assignment) {
        assignment.setStatus(1);
        assignment.setCreateTime(LocalDateTime.now());
        assignmentRepository.save(assignment);
    }
    
    @Override
    public void update(AssignmentInfo assignment) {
        AssignmentInfo existing = assignmentRepository.findById(assignment.getId())
            .orElseThrow(() -> new BusinessException("作业不存在"));
        
        existing.setTitle(assignment.getTitle());
        existing.setDescription(assignment.getDescription());
        existing.setDeadline(assignment.getDeadline());
        existing.setCourseId(assignment.getCourseId());
        existing.setStatus(assignment.getStatus());
        assignmentRepository.save(existing);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        assignmentRepository.deleteById(id);
        submitRepository.deleteByAssignmentId(id);
    }

    @Override
    public List<Map<String, Object>> getSubmits(Long assignmentId) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<AssignmentSubmit> submits = submitRepository.findByAssignmentId(assignmentId);
        
        for (AssignmentSubmit submit : submits) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", submit.getId());
            map.put("status", submit.getStatus());
            map.put("submitTime", submit.getSubmitTime());
            map.put("remark", submit.getRemark());
            
            studentRepository.findById(submit.getStudentId()).ifPresent(student -> {
                map.put("studentNo", student.getStudentNo());
                if (student.getUserId() != null) {
                    userRepository.findById(student.getUserId()).ifPresent(user -> {
                        map.put("studentName", user.getRealName());
                    });
                }
            });
            result.add(map);
        }
        return result;
    }
    
    @Override
    public void recordSubmit(AssignmentSubmit submit) {
        Optional<AssignmentSubmit> existing = submitRepository
            .findByAssignmentIdAndStudentId(submit.getAssignmentId(), submit.getStudentId());
        
        if (existing.isPresent()) {
            AssignmentSubmit record = existing.get();
            record.setStatus(submit.getStatus());
            record.setSubmitTime(LocalDateTime.now());
            record.setRemark(submit.getRemark());
            submitRepository.save(record);
        } else {
            submit.setSubmitTime(LocalDateTime.now());
            submitRepository.save(submit);
        }
    }
    
    @Override
    public void updateSubmit(Long submitId, AssignmentSubmit submit) {
        AssignmentSubmit existing = submitRepository.findById(submitId)
            .orElseThrow(() -> new BusinessException("提交记录不存在"));
        existing.setStatus(submit.getStatus());
        existing.setRemark(submit.getRemark());
        submitRepository.save(existing);
    }
    
    @Override
    public List<Map<String, Object>> getStudentAssignments(Long studentId) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<AssignmentInfo> assignments = assignmentRepository.findByStatus(1);
        
        for (AssignmentInfo assignment : assignments) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", assignment.getId());
            map.put("title", assignment.getTitle());
            map.put("description", assignment.getDescription());
            map.put("deadline", assignment.getDeadline());
            
            courseRepository.findById(assignment.getCourseId()).ifPresent(course -> {
                map.put("courseName", course.getCourseName());
            });
            
            Optional<AssignmentSubmit> submit = submitRepository
                .findByAssignmentIdAndStudentId(assignment.getId(), studentId);
            map.put("submitStatus", submit.map(AssignmentSubmit::getStatus).orElse(SubmitStatus.NOT_SUBMITTED.getCode()));
            map.put("submitTime", submit.map(AssignmentSubmit::getSubmitTime).orElse(null));
            
            result.add(map);
        }
        return result;
    }
    
    @Override
    public Map<String, Object> getClassStatistics(Long classId) {
        Map<String, Object> result = new HashMap<>();
        
        List<StudentInfo> students = studentRepository.findByClassId(classId);
        if (students.isEmpty()) {
            result.put("totalAssignments", 0);
            result.put("completionRate", 0);
            return result;
        }
        
        List<Long> studentIds = students.stream().map(StudentInfo::getId).toList();
        List<AssignmentInfo> assignments = assignmentRepository.findByStatus(1);
        
        long totalExpected = (long) assignments.size() * students.size();
        long submitted = submitRepository.countByStudentIdInAndStatus(studentIds, SubmitStatus.SUBMITTED.getCode());
        
        result.put("totalAssignments", assignments.size());
        result.put("studentCount", students.size());
        result.put("submitted", submitted);
        result.put("completionRate", totalExpected > 0 ? (double) submitted / totalExpected * 100 : 0);
        
        return result;
    }
    
    private void fillAssignmentInfo(AssignmentInfo assignment) {
        if (assignment.getCourseId() != null) {
            courseRepository.findById(assignment.getCourseId()).ifPresent(course -> {
                assignment.setCourseName(course.getCourseName());
            });
        }
    }
}
