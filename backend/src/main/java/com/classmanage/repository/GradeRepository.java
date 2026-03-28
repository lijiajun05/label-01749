package com.classmanage.repository;

import com.classmanage.entity.GradeInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 成绩 Repository
 */
@Repository
public interface GradeRepository extends JpaRepository<GradeInfo, Long> {
    
    List<GradeInfo> findByStudentId(Long studentId);
    
    List<GradeInfo> findByCourseId(Long courseId);
    
    List<GradeInfo> findByStudentIdIn(List<Long> studentIds);
    
    List<GradeInfo> findByStudentIdAndGradeType(Long studentId, Integer gradeType);
    
    @Query("SELECT g FROM GradeInfo g WHERE " +
           "(:studentId IS NULL OR g.studentId = :studentId) AND " +
           "(:courseId IS NULL OR g.courseId = :courseId) AND " +
           "(:semester IS NULL OR g.semester = :semester)")
    Page<GradeInfo> findByConditions(@Param("studentId") Long studentId,
                                      @Param("courseId") Long courseId,
                                      @Param("semester") String semester,
                                      Pageable pageable);
    
    @Query("SELECT COUNT(DISTINCT g.courseId) FROM GradeInfo g " +
           "WHERE g.studentId = :studentId AND g.gradeType = 3 AND g.score < 60 " +
           "AND (:semester IS NULL OR g.semester = :semester)")
    int countFailCourses(@Param("studentId") Long studentId, @Param("semester") String semester);
}
