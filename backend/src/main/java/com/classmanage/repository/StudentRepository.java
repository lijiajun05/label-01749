package com.classmanage.repository;

import com.classmanage.entity.StudentInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学生 Repository
 */
@Repository
public interface StudentRepository extends JpaRepository<StudentInfo, Long> {
    
    boolean existsByStudentNo(String studentNo);
    
    Optional<StudentInfo> findByUserId(Long userId);
    
    List<StudentInfo> findByClassId(Long classId);
    
    long countByClassId(Long classId);
    
    @Query("SELECT s FROM StudentInfo s WHERE " +
           "(:studentNo IS NULL OR s.studentNo LIKE %:studentNo%) AND " +
           "(:classId IS NULL OR s.classId = :classId)")
    Page<StudentInfo> findByConditions(@Param("studentNo") String studentNo,
                                        @Param("classId") Long classId,
                                        Pageable pageable);
}
