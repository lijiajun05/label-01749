package com.classmanage.service.impl;

import com.classmanage.common.enums.AttendanceStatus;
import com.classmanage.common.exception.BusinessException;
import com.classmanage.entity.AttendanceRecord;
import com.classmanage.entity.CourseInfo;
import com.classmanage.entity.StudentInfo;
import com.classmanage.entity.User;
import com.classmanage.repository.*;
import com.classmanage.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    
    @Override
    public Page<AttendanceRecord> getPage(Integer pageNum, Integer pageSize, Long studentId, Long courseId, String date) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "attendanceDate"));
        LocalDate localDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        Page<AttendanceRecord> page = attendanceRepository.findByConditions(studentId, courseId, localDate, localDate, pageRequest);
        
        // 填充关联信息
        page.getContent().forEach(this::fillAttendanceInfo);
        return page;
    }
    
    @Override
    public List<AttendanceRecord> getByStudentId(Long studentId) {
        List<AttendanceRecord> records = attendanceRepository.findByStudentIdOrderByAttendanceDateDesc(studentId);
        records.forEach(this::fillAttendanceInfo);
        return records;
    }
    
    @Override
    public List<AttendanceRecord> getByCourseId(Long courseId) {
        return attendanceRepository.findByCourseIdOrderByAttendanceDateDesc(courseId);
    }
    
    @Override
    public void create(AttendanceRecord attendance) {
        attendance.setCreateTime(LocalDateTime.now());
        attendanceRepository.save(attendance);
    }
    
    @Override
    public void batchCreate(List<AttendanceRecord> attendances) {
        for (AttendanceRecord attendance : attendances) {
            attendance.setCreateTime(LocalDateTime.now());
        }
        attendanceRepository.saveAll(attendances);
    }

    @Override
    public void update(AttendanceRecord attendance) {
        AttendanceRecord existing = attendanceRepository.findById(attendance.getId())
            .orElseThrow(() -> new BusinessException("考勤记录不存在"));
        
        existing.setStatus(attendance.getStatus());
        existing.setRemark(attendance.getRemark());
        attendanceRepository.save(existing);
    }
    
    @Override
    public void delete(Long id) {
        attendanceRepository.deleteById(id);
    }
    
    @Override
    public Map<String, Object> getStudentStatistics(Long studentId) {
        Map<String, Object> result = new HashMap<>();
        List<AttendanceRecord> records = attendanceRepository.findByStudentIdOrderByAttendanceDateDesc(studentId);
        
        long total = records.size();
        long present = records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT.getCode()).count();
        long late = records.stream().filter(r -> r.getStatus() == AttendanceStatus.LATE.getCode()).count();
        long absent = records.stream().filter(r -> r.getStatus() == AttendanceStatus.ABSENT.getCode()).count();
        long leave = records.stream().filter(r -> r.getStatus() == AttendanceStatus.LEAVE.getCode()).count();
        
        result.put("total", total);
        result.put("present", present);
        result.put("late", late);
        result.put("absent", absent);
        result.put("leave", leave);
        result.put("attendanceRate", total > 0 ? (double) (present + late) / total * 100 : 0);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getClassStatistics(Long classId) {
        Map<String, Object> result = new HashMap<>();
        
        List<StudentInfo> students = studentRepository.findByClassId(classId);
        if (students.isEmpty()) {
            result.put("studentCount", 0);
            result.put("averageAttendanceRate", 0);
            return result;
        }
        
        List<Long> studentIds = students.stream().map(StudentInfo::getId).toList();
        List<AttendanceRecord> records = attendanceRepository.findByStudentIdIn(studentIds);
        
        long total = records.size();
        long present = records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT.getCode()).count();
        long late = records.stream().filter(r -> r.getStatus() == AttendanceStatus.LATE.getCode()).count();
        
        result.put("studentCount", students.size());
        result.put("totalRecords", total);
        result.put("averageAttendanceRate", total > 0 ? (double) (present + late) / total * 100 : 0);
        
        return result;
    }
    
    private void fillAttendanceInfo(AttendanceRecord record) {
        if (record.getStudentId() != null) {
            studentRepository.findById(record.getStudentId()).ifPresent(student -> {
                record.setStudentNo(student.getStudentNo());
                if (student.getUserId() != null) {
                    userRepository.findById(student.getUserId()).ifPresent(user -> {
                        record.setStudentName(user.getRealName());
                    });
                }
            });
        }
        if (record.getCourseId() != null) {
            courseRepository.findById(record.getCourseId()).ifPresent(course -> {
                record.setCourseName(course.getCourseName());
            });
        }
    }
}
