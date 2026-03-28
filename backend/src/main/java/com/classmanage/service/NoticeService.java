package com.classmanage.service;

import com.classmanage.entity.NoticeInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface NoticeService {
    
    Page<NoticeInfo> getPage(Integer pageNum, Integer pageSize, Long classId, Integer noticeType, Integer status);
    
    NoticeInfo getById(Long id);
    
    void create(NoticeInfo notice);
    
    void update(NoticeInfo notice);
    
    void delete(Long id);
    
    void setTop(Long id, boolean isTop);
    
    void withdraw(Long id);
    
    void markAsRead(Long noticeId, Long userId);
    
    List<Map<String, Object>> getReadStatus(Long noticeId);
    
    int getUnreadCount(Long userId);
}
