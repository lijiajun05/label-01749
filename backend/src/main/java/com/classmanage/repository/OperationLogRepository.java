package com.classmanage.repository;

import com.classmanage.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 操作日志 Repository
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    
    @Query("SELECT o FROM OperationLog o WHERE " +
           "(:username IS NULL OR o.username LIKE %:username%) AND " +
           "(:operationType IS NULL OR o.operationType = :operationType) AND " +
           "(:startTime IS NULL OR o.createTime >= :startTime) AND " +
           "(:endTime IS NULL OR o.createTime <= :endTime) " +
           "ORDER BY o.createTime DESC")
    Page<OperationLog> findByConditions(@Param("username") String username,
                                         @Param("operationType") String operationType,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         Pageable pageable);
}
