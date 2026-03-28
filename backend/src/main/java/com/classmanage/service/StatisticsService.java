package com.classmanage.service;

import java.util.Map;

public interface StatisticsService {
    
    Map<String, Object> getDashboardData(Long classId);
    
    Map<String, Object> getAttendanceTrend(Long classId, String startDate, String endDate);
    
    Map<String, Object> getGradeDistribution(Long classId);
    
    Map<String, Object> getAssignmentCompletion(Long classId);
    
    Map<String, Object> getClassStudyStyle(Long classId);
    
    Map<String, Object> getStudentStudyStyle(Long studentId);
}
