package com.classmanage.repository;

import com.classmanage.entity.CourseInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程 Repository
 */
@Repository
public interface CourseRepository extends JpaRepository<CourseInfo, Long> {
    
    boolean existsByCourseCode(String courseCode);
    
    List<CourseInfo> findByStatus(Integer status);
    
    @Query("SELECT c FROM CourseInfo c WHERE " +
           "(:courseName IS NULL OR c.courseName LIKE %:courseName%) AND " +
           "(:teacherId IS NULL OR c.teacherId = :teacherId) AND " +
           "(:semester IS NULL OR c.semester = :semester)")
    Page<CourseInfo> findByConditions(@Param("courseName") String courseName,
                                       @Param("teacherId") Long teacherId,
                                       @Param("semester") String semester,
                                       Pageable pageable);
}
