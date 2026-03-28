package com.classmanage.repository;

import com.classmanage.entity.NoticeRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 通知阅读记录 Repository
 */
@Repository
public interface NoticeReadRepository extends JpaRepository<NoticeRead, Long> {
    
    Optional<NoticeRead> findByNoticeIdAndUserId(Long noticeId, Long userId);
    
    List<NoticeRead> findByNoticeId(Long noticeId);
    
    void deleteByNoticeId(Long noticeId);
    
    long countByUserIdAndNoticeIdIn(Long userId, List<Long> noticeIds);
}
