package com.classmanage.repository;

import com.classmanage.entity.NoticeInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 通知 Repository
 */
@Repository
public interface NoticeRepository extends JpaRepository<NoticeInfo, Long> {
    
    @Query("SELECT n FROM NoticeInfo n WHERE " +
           "(:classId IS NULL OR n.classId = :classId) AND " +
           "(:noticeType IS NULL OR n.noticeType = :noticeType) AND " +
           "(:status IS NULL OR n.status = :status) " +
           "ORDER BY n.isTop DESC, n.publishTime DESC")
    Page<NoticeInfo> findByConditions(@Param("classId") Long classId,
                                       @Param("noticeType") Integer noticeType,
                                       @Param("status") Integer status,
                                       Pageable pageable);
    
    @Query("SELECT n FROM NoticeInfo n WHERE n.status = 1 AND " +
           "(n.classId IS NULL OR n.classId = :classId)")
    List<NoticeInfo> findVisibleNotices(@Param("classId") Long classId);
    
    long countByStatus(Integer status);
}
