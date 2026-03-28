package com.classmanage.repository;

import com.classmanage.entity.ClassCommittee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 班委 Repository
 */
@Repository
public interface CommitteeRepository extends JpaRepository<ClassCommittee, Long> {
    
    List<ClassCommittee> findByClassIdAndStatusOrderByPosition(Long classId, Integer status);
    
    List<ClassCommittee> findByStatusOrderByClassIdAscPositionAsc(Integer status);
}
