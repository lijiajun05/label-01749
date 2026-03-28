package com.classmanage.repository;

import com.classmanage.entity.ClassMeeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 班会 Repository
 */
@Repository
public interface MeetingRepository extends JpaRepository<ClassMeeting, Long> {
    
    @Query("SELECT m FROM ClassMeeting m WHERE " +
           "(:classId IS NULL OR m.classId = :classId) " +
           "ORDER BY m.meetingTime DESC")
    Page<ClassMeeting> findByConditions(@Param("classId") Long classId, Pageable pageable);
}
