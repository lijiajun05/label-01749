package com.classmanage.service.impl;

import com.classmanage.common.enums.AttendanceStatus;
import com.classmanage.common.enums.SubmitStatus;
import com.classmanage.common.enums.WarningType;
import com.classmanage.common.exception.BusinessException;
import com.classmanage.entity.*;
import com.classmanage.repository.*;
import com.classmanage.service.WarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarningServiceImpl implements WarningService {
    
    private final WarningRepository warningRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmitRepository submitRepository;
    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    
    private static final int ASSIGNMENT_MISS_THRESHOLD = 3;
    private static final double ATTENDANCE_RATE_THRESHOLD = 0.8;
    private static final int FAIL_COURSE_THRESHOLD = 2;
    
    @Override
    public Page<WarningRecord> getPage(Integer pageNum, Integer pageSize, Long studentId, Integer warningType, Integer status) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);
        Page<WarningRecord> page = warningRepository.findByConditions(studentId, warningType, status, pageRequest);
        fillStudentInfo(page.getContent());
        return page;
    }
    
    @Override
    public WarningRecord getById(Long id) {
        WarningRecord warning = warningRepository.findById(id)
            .orElseThrow(() -> new BusinessException("预警记录不存在"));
        fillStudentInfo(List.of(warning));
        return warning;
    }
    
    @Override
    public List<WarningRecord> getByStudentId(Long studentId) {
        List<WarningRecord> list = warningRepository.findByStudentIdOrderByCreateTimeDesc(studentId);
        fillStudentInfo(list);
        return list;
    }
    
    @Override
    public List<WarningRecord> getByClassId(Long classId) {
        List<StudentInfo> students = studentRepository.findByClassId(classId);
        if (students.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> studentIds = students.stream().map(StudentInfo::getId).toList();
        List<WarningRecord> list = warningRepository.findByStudentIdInOrderByCreateTimeDesc(studentIds);
        fillStudentInfo(list);
        return list;
    }

    @Override
    public void handleWarning(Long id, Long handlerId, String handleResult) {
        WarningRecord warning = warningRepository.findById(id)
            .orElseThrow(() -> new BusinessException("预警记录不存在"));
        warning.setStatus(1);
        warning.setHandlerId(handlerId);
        warning.setHandleResult(handleResult);
        warning.setHandleTime(LocalDateTime.now());
        warningRepository.save(warning);
    }
    
    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void generateWarnings() {
        log.info("开始生成预警...");
        List<StudentInfo> students = studentRepository.findAll();
        
        for (StudentInfo student : students) {
            checkAssignmentWarning(student);
            checkAttendanceWarning(student);
            checkGradeWarning(student);
        }
        log.info("预警生成完成");
    }
    
    private void checkAssignmentWarning(StudentInfo student) {
        List<AssignmentInfo> assignments = assignmentRepository.findByStatus(1);
        
        int missCount = 0;
        for (AssignmentInfo assignment : assignments) {
            Optional<AssignmentSubmit> submit = submitRepository
                .findByAssignmentIdAndStudentId(assignment.getId(), student.getId());
            if (submit.isEmpty() || submit.get().getStatus() != SubmitStatus.SUBMITTED.getCode()) {
                missCount++;
            }
        }
        
        if (missCount >= ASSIGNMENT_MISS_THRESHOLD) {
            createWarningIfNotExists(student.getId(), WarningType.ASSIGNMENT_MISS,
                "作业长期未交预警", "该学生有" + missCount + "次作业未提交");
        }
    }
    
    private void checkAttendanceWarning(StudentInfo student) {
        List<AttendanceRecord> records = attendanceRepository.findByStudentIdOrderByAttendanceDateDesc(student.getId());
        
        if (records.isEmpty()) return;
        
        long presentCount = records.stream()
            .filter(r -> r.getStatus() == AttendanceStatus.PRESENT.getCode() 
                      || r.getStatus() == AttendanceStatus.LATE.getCode())
            .count();
        
        double rate = (double) presentCount / records.size();
        
        if (rate < ATTENDANCE_RATE_THRESHOLD) {
            createWarningIfNotExists(student.getId(), WarningType.LOW_ATTENDANCE,
                "出勤率偏低预警", "该学生出勤率为" + String.format("%.1f", rate * 100) + "%，低于80%");
        }
    }
    
    private void checkGradeWarning(StudentInfo student) {
        List<GradeInfo> grades = gradeRepository.findByStudentIdAndGradeType(student.getId(), 3);
        
        long failCount = grades.stream()
            .filter(g -> g.getScore().compareTo(new BigDecimal("60")) < 0)
            .count();
        
        if (failCount >= FAIL_COURSE_THRESHOLD) {
            createWarningIfNotExists(student.getId(), WarningType.FAIL_RISK,
                "挂科风险预警", "该学生有" + failCount + "门课程不及格");
        }
    }
    
    private void createWarningIfNotExists(Long studentId, WarningType type, String title, String content) {
        if (warningRepository.existsByStudentIdAndWarningTypeAndStatus(studentId, type.getCode(), 0)) {
            return;
        }
        
        WarningRecord warning = new WarningRecord();
        warning.setStudentId(studentId);
        warning.setWarningType(type.getCode());
        warning.setTitle(title);
        warning.setContent(content);
        warning.setStatus(0);
        warning.setCreateTime(LocalDateTime.now());
        warningRepository.save(warning);
    }
    
    @Override
    public Map<String, Object> getClassStatistics(Long classId) {
        Map<String, Object> result = new HashMap<>();
        List<WarningRecord> warnings = getByClassId(classId);
        
        long total = warnings.size();
        long unhandled = warnings.stream().filter(w -> w.getStatus() == 0).count();
        long assignmentWarnings = warnings.stream().filter(w -> w.getWarningType() == WarningType.ASSIGNMENT_MISS.getCode()).count();
        long attendanceWarnings = warnings.stream().filter(w -> w.getWarningType() == WarningType.LOW_ATTENDANCE.getCode()).count();
        long gradeWarnings = warnings.stream().filter(w -> w.getWarningType() == WarningType.FAIL_RISK.getCode()).count();
        
        result.put("total", total);
        result.put("unhandled", unhandled);
        result.put("assignmentWarnings", assignmentWarnings);
        result.put("attendanceWarnings", attendanceWarnings);
        result.put("gradeWarnings", gradeWarnings);
        
        return result;
    }
    
    private void fillStudentInfo(List<WarningRecord> warnings) {
        if (warnings == null || warnings.isEmpty()) return;
        
        List<Long> studentIds = warnings.stream()
            .map(WarningRecord::getStudentId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        if (studentIds.isEmpty()) return;
        
        List<StudentInfo> students = studentRepository.findAllById(studentIds);
        Map<Long, StudentInfo> studentMap = new HashMap<>();
        for (StudentInfo s : students) {
            studentMap.put(s.getId(), s);
        }
        
        // 获取用户信息（姓名）
        List<Long> userIds = students.stream()
            .map(StudentInfo::getUserId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        Map<Long, String> userNameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userRepository.findAllById(userIds);
            for (User u : users) {
                userNameMap.put(u.getId(), u.getRealName());
            }
        }
        
        // 获取班级信息
        List<Long> classIds = students.stream()
            .map(StudentInfo::getClassId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        Map<Long, String> classNameMap = new HashMap<>();
        if (!classIds.isEmpty()) {
            List<ClassInfo> classes = classRepository.findAllById(classIds);
            for (ClassInfo c : classes) {
                classNameMap.put(c.getId(), c.getClassName());
            }
        }
        
        for (WarningRecord w : warnings) {
            StudentInfo student = studentMap.get(w.getStudentId());
            if (student != null) {
                w.setStudentNo(student.getStudentNo());
                if (student.getUserId() != null) {
                    w.setStudentName(userNameMap.get(student.getUserId()));
                }
                if (student.getClassId() != null) {
                    w.setClassName(classNameMap.get(student.getClassId()));
                }
            }
        }
    }
}
