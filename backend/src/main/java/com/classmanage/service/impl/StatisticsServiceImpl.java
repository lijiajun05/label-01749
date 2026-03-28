package com.classmanage.service.impl;

import com.classmanage.common.enums.AttendanceStatus;
import com.classmanage.common.enums.SubmitStatus;
import com.classmanage.entity.*;
import com.classmanage.repository.*;
import com.classmanage.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final AttendanceRepository attendanceRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmitRepository submitRepository;
    private final GradeRepository gradeRepository;
    private final WarningRepository warningRepository;
    private final NoticeRepository noticeRepository;
    
    @Override
    public Map<String, Object> getDashboardData(Long classId) {
        Map<String, Object> result = new HashMap<>();
        
        long studentCount;
        if (classId != null) {
            studentCount = studentRepository.countByClassId(classId);
        } else {
            studentCount = studentRepository.count();
        }
        result.put("studentCount", studentCount);
        
        long classCount = classRepository.count();
        result.put("classCount", classCount);
        
        long warningCount;
        if (classId != null) {
            List<StudentInfo> students = studentRepository.findByClassId(classId);
            if (!students.isEmpty()) {
                List<Long> studentIds = students.stream().map(StudentInfo::getId).toList();
                warningCount = warningRepository.countByStudentIdInAndStatus(studentIds, 0);
            } else {
                warningCount = 0;
            }
        } else {
            warningCount = warningRepository.countByStatus(0);
        }
        result.put("warningCount", warningCount);
        
        long noticeCount = noticeRepository.countByStatus(1);
        result.put("noticeCount", noticeCount);
        
        return result;
    }

    @Override
    public Map<String, Object> getAttendanceTrend(Long classId, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        List<Long> studentIds = getStudentIdsByClass(classId);
        if (studentIds.isEmpty()) {
            result.put("dates", new ArrayList<>());
            result.put("rates", new ArrayList<>());
            return result;
        }
        
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;
        
        List<AttendanceRecord> records = attendanceRepository.findByStudentIdsAndDateRange(studentIds, start, end);
        
        Map<LocalDate, List<AttendanceRecord>> grouped = records.stream()
            .collect(Collectors.groupingBy(AttendanceRecord::getAttendanceDate));
        
        List<String> dates = new ArrayList<>();
        List<Double> rates = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        for (Map.Entry<LocalDate, List<AttendanceRecord>> entry : grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()).toList()) {
            dates.add(entry.getKey().format(formatter));
            List<AttendanceRecord> dayRecords = entry.getValue();
            long present = dayRecords.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.PRESENT.getCode() 
                          || r.getStatus() == AttendanceStatus.LATE.getCode())
                .count();
            rates.add(dayRecords.isEmpty() ? 0 : (double) present / dayRecords.size() * 100);
        }
        
        result.put("dates", dates);
        result.put("rates", rates);
        return result;
    }
    
    @Override
    public Map<String, Object> getGradeDistribution(Long classId) {
        Map<String, Object> result = new HashMap<>();
        
        List<Long> studentIds = getStudentIdsByClass(classId);
        if (studentIds.isEmpty()) {
            result.put("distribution", new ArrayList<>());
            return result;
        }
        
        List<GradeInfo> grades = gradeRepository.findByStudentIdIn(studentIds);
        
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
        String[] labels = {"不及格", "及格", "中等", "良好", "优秀"};
        for (int i = 0; i < 5; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", labels[i]);
            item.put("value", ranges[i]);
            distribution.add(item);
        }
        
        result.put("distribution", distribution);
        return result;
    }
    
    @Override
    public Map<String, Object> getAssignmentCompletion(Long classId) {
        Map<String, Object> result = new HashMap<>();
        
        List<Long> studentIds = getStudentIdsByClass(classId);
        if (studentIds.isEmpty()) {
            result.put("completed", 0);
            result.put("notCompleted", 0);
            return result;
        }
        
        List<AssignmentInfo> assignments = assignmentRepository.findByStatus(1);
        long totalExpected = (long) assignments.size() * studentIds.size();
        long submitted = submitRepository.countByStudentIdInAndStatus(studentIds, SubmitStatus.SUBMITTED.getCode());
        
        result.put("completed", submitted);
        result.put("notCompleted", totalExpected - submitted);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getClassStudyStyle(Long classId) {
        Map<String, Object> result = new HashMap<>();
        
        List<Long> studentIds = getStudentIdsByClass(classId);
        if (studentIds.isEmpty()) {
            result.put("attendanceRate", 0);
            result.put("assignmentRate", 0);
            result.put("averageScore", 0);
            result.put("studyStyleScore", 0);
            return result;
        }
        
        List<AttendanceRecord> attRecords = attendanceRepository.findByStudentIdIn(studentIds);
        long present = attRecords.stream()
            .filter(r -> r.getStatus() == AttendanceStatus.PRESENT.getCode() 
                      || r.getStatus() == AttendanceStatus.LATE.getCode())
            .count();
        double attendanceRate = attRecords.isEmpty() ? 0 : (double) present / attRecords.size() * 100;
        
        List<AssignmentInfo> assignments = assignmentRepository.findByStatus(1);
        long totalExpected = (long) assignments.size() * studentIds.size();
        long submitted = submitRepository.countByStudentIdInAndStatus(studentIds, SubmitStatus.SUBMITTED.getCode());
        double assignmentRate = totalExpected == 0 ? 0 : (double) submitted / totalExpected * 100;
        
        List<GradeInfo> grades = gradeRepository.findByStudentIdIn(studentIds);
        double averageScore = grades.stream()
            .mapToDouble(g -> g.getScore().doubleValue())
            .average()
            .orElse(0);
        
        double studyStyleScore = attendanceRate * 0.3 + assignmentRate * 0.3 + averageScore * 0.4;
        
        result.put("attendanceRate", Math.round(attendanceRate * 100) / 100.0);
        result.put("assignmentRate", Math.round(assignmentRate * 100) / 100.0);
        result.put("averageScore", Math.round(averageScore * 100) / 100.0);
        result.put("studyStyleScore", Math.round(studyStyleScore * 100) / 100.0);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getStudentStudyStyle(Long studentId) {
        Map<String, Object> result = new HashMap<>();
        
        List<AttendanceRecord> attRecords = attendanceRepository.findByStudentIdOrderByAttendanceDateDesc(studentId);
        long present = attRecords.stream()
            .filter(r -> r.getStatus() == AttendanceStatus.PRESENT.getCode() 
                      || r.getStatus() == AttendanceStatus.LATE.getCode())
            .count();
        double attendanceRate = attRecords.isEmpty() ? 0 : (double) present / attRecords.size() * 100;
        
        List<AssignmentInfo> assignments = assignmentRepository.findByStatus(1);
        long submitted = submitRepository.countByStudentIdAndStatus(studentId, SubmitStatus.SUBMITTED.getCode());
        double assignmentRate = assignments.isEmpty() ? 0 : (double) submitted / assignments.size() * 100;
        
        List<GradeInfo> grades = gradeRepository.findByStudentId(studentId);
        double averageScore = grades.stream()
            .mapToDouble(g -> g.getScore().doubleValue())
            .average()
            .orElse(0);
        
        double studyStyleScore = attendanceRate * 0.3 + assignmentRate * 0.3 + averageScore * 0.4;
        
        result.put("attendanceRate", Math.round(attendanceRate * 100) / 100.0);
        result.put("assignmentRate", Math.round(assignmentRate * 100) / 100.0);
        result.put("averageScore", Math.round(averageScore * 100) / 100.0);
        result.put("studyStyleScore", Math.round(studyStyleScore * 100) / 100.0);
        
        return result;
    }
    
    private List<Long> getStudentIdsByClass(Long classId) {
        List<StudentInfo> students;
        if (classId != null) {
            students = studentRepository.findByClassId(classId);
        } else {
            students = studentRepository.findAll();
        }
        return students.stream().map(StudentInfo::getId).toList();
    }
}
