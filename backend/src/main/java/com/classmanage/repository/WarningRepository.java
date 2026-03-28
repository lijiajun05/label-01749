package com.classmanage.repository;

import com.classmanage.entity.WarningRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 预警 Repository
 */
@Repository
public interface WarningRepository extends JpaRepository<WarningRecord, Long> {
    
    List<WarningRecord> findByStudentIdOrderByCreateTimeDesc(Long studentId);
    
    List<WarningRecord> findByStudentIdInOrderByCreateTimeDesc(List<Long> studentIds);
    
    boolean existsByStudentIdAndWarningTypeAndStatus(Long studentId, Integer warningType, Integer status);
    
    long countByStatus(Integer status);
    
    long countByStudentIdInAndStatus(List<Long> studentIds, Integer status);
    
    @Query("SELECT w FROM WarningRecord w WHERE " +
           "(:studentId IS NULL OR w.studentId = :studentId) AND " +
           "(:warningType IS NULL OR w.warningType = :warningType) AND " +
           "(:status IS NULL OR w.status = :status) " +
           "ORDER BY w.createTime DESC")
    Page<WarningRecord> findByConditions(@Param("studentId") Long studentId,
                                          @Param("warningType") Integer warningType,
                                          @Param("status") Integer status,
                                          Pageable pageable);
}
