package com.classmanage.repository;

import com.classmanage.entity.AttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤 Repository
 */
@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    
    List<AttendanceRecord> findByStudentIdOrderByAttendanceDateDesc(Long studentId);
    
    List<AttendanceRecord> findByCourseIdOrderByAttendanceDateDesc(Long courseId);
    
    List<AttendanceRecord> findByStudentIdIn(List<Long> studentIds);
    
    @Query("SELECT a FROM AttendanceRecord a WHERE " +
           "(:studentId IS NULL OR a.studentId = :studentId) AND " +
           "(:courseId IS NULL OR a.courseId = :courseId) AND " +
           "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.attendanceDate <= :endDate) " +
           "ORDER BY a.attendanceDate DESC")
    Page<AttendanceRecord> findByConditions(@Param("studentId") Long studentId,
                                             @Param("courseId") Long courseId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate,
                                             Pageable pageable);
    
    @Query("SELECT a FROM AttendanceRecord a WHERE a.studentId IN :studentIds " +
           "AND (:startDate IS NULL OR a.attendanceDate >= :startDate) " +
           "AND (:endDate IS NULL OR a.attendanceDate <= :endDate)")
    List<AttendanceRecord> findByStudentIdsAndDateRange(@Param("studentIds") List<Long> studentIds,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);
}
