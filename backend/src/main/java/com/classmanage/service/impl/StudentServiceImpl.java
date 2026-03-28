package com.classmanage.service.impl;

import com.classmanage.common.constant.RoleConstants;
import com.classmanage.common.exception.BusinessException;
import com.classmanage.entity.ClassInfo;
import com.classmanage.entity.StudentInfo;
import com.classmanage.entity.User;
import com.classmanage.repository.ClassRepository;
import com.classmanage.repository.StudentRepository;
import com.classmanage.repository.UserRepository;
import com.classmanage.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public Page<Map<String, Object>> getPage(Integer pageNum, Integer pageSize, String studentNo, String realName, Long classId) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<StudentInfo> studentPage = studentRepository.findByConditions(
            StringUtils.hasText(studentNo) ? studentNo : null,
            classId,
            pageRequest
        );
        
        List<Map<String, Object>> records = new ArrayList<>();
        for (StudentInfo student : studentPage.getContent()) {
            Map<String, Object> map = buildStudentMap(student);
            if (StringUtils.hasText(realName)) {
                String name = (String) map.get("realName");
                if (name == null || !name.contains(realName)) {
                    continue;
                }
            }
            records.add(map);
        }
        
        return new PageImpl<>(records, pageRequest, studentPage.getTotalElements());
    }
    
    @Override
    public Map<String, Object> getById(Long id) {
        StudentInfo student = studentRepository.findById(id)
            .orElseThrow(() -> new BusinessException("学生不存在"));
        return buildStudentMap(student);
    }
    
    @Override
    public Map<String, Object> getByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
            .map(this::buildStudentMap)
            .orElse(null);
    }
    
    @Override
    public List<Map<String, Object>> getAll() {
        List<StudentInfo> students = studentRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentInfo student : students) {
            result.add(buildStudentMap(student));
        }
        return result;
    }

    @Override
    @Transactional
    public void create(StudentInfo student, String realName, String phone, String email) {
        if (!StringUtils.hasText(realName)) {
            throw new BusinessException("姓名不能为空");
        }
        validatePhone(phone);
        validateEmail(email);
        
        if (studentRepository.existsByStudentNo(student.getStudentNo())) {
            throw new BusinessException("学号已存在");
        }
        
        User user = new User();
        user.setUsername(student.getStudentNo());
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRealName(realName);
        user.setPhone(phone);
        user.setEmail(email);
        user.setRoleId(RoleConstants.ROLE_ID_STUDENT);
        user.setClassId(student.getClassId());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        
        student.setUserId(user.getId());
        student.setCreateTime(LocalDateTime.now());
        studentRepository.save(student);
        
        updateClassStudentCount(student.getClassId());
    }
    
    @Override
    @Transactional
    public void update(Long id, StudentInfo student, String realName, String phone, String email) {
        if (!StringUtils.hasText(realName)) {
            throw new BusinessException("姓名不能为空");
        }
        validatePhone(phone);
        validateEmail(email);
        
        StudentInfo existing = studentRepository.findById(id)
            .orElseThrow(() -> new BusinessException("学生不存在"));
        
        if (existing.getUserId() != null) {
            userRepository.findById(existing.getUserId()).ifPresent(user -> {
                user.setRealName(realName);
                user.setPhone(phone);
                user.setEmail(email);
                user.setClassId(student.getClassId());
                user.setUpdateTime(LocalDateTime.now());
                userRepository.save(user);
            });
        }
        
        Long oldClassId = existing.getClassId();
        existing.setStudentNo(student.getStudentNo());
        existing.setClassId(student.getClassId());
        existing.setGender(student.getGender());
        existing.setEnrollmentDate(student.getEnrollmentDate());
        studentRepository.save(existing);
        
        if (!Objects.equals(oldClassId, student.getClassId())) {
            updateClassStudentCount(oldClassId);
            updateClassStudentCount(student.getClassId());
        }
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        StudentInfo student = studentRepository.findById(id)
            .orElseThrow(() -> new BusinessException("学生不存在"));
        
        if (student.getUserId() != null) {
            userRepository.deleteById(student.getUserId());
        }
        
        studentRepository.deleteById(id);
        updateClassStudentCount(student.getClassId());
    }
    
    private Map<String, Object> buildStudentMap(StudentInfo student) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", student.getId());
        map.put("studentNo", student.getStudentNo());
        map.put("classId", student.getClassId());
        map.put("gender", student.getGender());
        map.put("enrollmentDate", student.getEnrollmentDate());
        
        if (student.getUserId() != null) {
            userRepository.findById(student.getUserId()).ifPresent(user -> {
                map.put("realName", user.getRealName());
                map.put("phone", user.getPhone());
                map.put("email", user.getEmail());
            });
        }
        
        if (student.getClassId() != null) {
            classRepository.findById(student.getClassId()).ifPresent(classInfo -> {
                map.put("className", classInfo.getClassName());
            });
        }
        return map;
    }
    
    private void updateClassStudentCount(Long classId) {
        if (classId == null) return;
        long count = studentRepository.countByClassId(classId);
        classRepository.findById(classId).ifPresent(classInfo -> {
            classInfo.setStudentCount((int) count);
            classRepository.save(classInfo);
        });
    }
    
    private void validatePhone(String phone) {
        if (StringUtils.hasText(phone) && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException("手机号格式不正确");
        }
    }
    
    private void validateEmail(String email) {
        if (StringUtils.hasText(email) && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException("邮箱格式不正确");
        }
    }
}
