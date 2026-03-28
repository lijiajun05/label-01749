package com.classmanage.repository;

import com.classmanage.entity.ClassInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 班级 Repository
 */
@Repository
public interface ClassRepository extends JpaRepository<ClassInfo, Long> {
    
    List<ClassInfo> findByStatus(Integer status);
    
    @Query("SELECT c FROM ClassInfo c WHERE " +
           "(:className IS NULL OR c.className LIKE %:className%) AND " +
           "(:grade IS NULL OR c.grade = :grade) AND " +
           "(:teacherId IS NULL OR c.teacherId = :teacherId)")
    Page<ClassInfo> findByConditions(@Param("className") String className,
                                      @Param("grade") String grade,
                                      @Param("teacherId") Long teacherId,
                                      Pageable pageable);
}
