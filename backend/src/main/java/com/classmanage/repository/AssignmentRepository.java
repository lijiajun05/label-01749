package com.classmanage.repository;

import com.classmanage.entity.AssignmentInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 作业 Repository
 */
@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentInfo, Long> {
    
    List<AssignmentInfo> findByStatus(Integer status);
    
    @Query("SELECT a FROM AssignmentInfo a WHERE " +
           "(:courseId IS NULL OR a.courseId = :courseId) AND " +
           "(:title IS NULL OR a.title LIKE %:title%)")
    Page<AssignmentInfo> findByConditions(@Param("courseId") Long courseId,
                                           @Param("title") String title,
                                           Pageable pageable);
}
