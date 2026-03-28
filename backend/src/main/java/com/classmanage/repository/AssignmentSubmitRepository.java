package com.classmanage.repository;

import com.classmanage.entity.AssignmentSubmit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 作业提交 Repository
 */
@Repository
public interface AssignmentSubmitRepository extends JpaRepository<AssignmentSubmit, Long> {
    
    List<AssignmentSubmit> findByAssignmentId(Long assignmentId);
    
    Optional<AssignmentSubmit> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    
    void deleteByAssignmentId(Long assignmentId);
    
    long countByStudentIdAndStatus(Long studentId, Integer status);
    
    long countByStudentIdInAndStatus(List<Long> studentIds, Integer status);
}
