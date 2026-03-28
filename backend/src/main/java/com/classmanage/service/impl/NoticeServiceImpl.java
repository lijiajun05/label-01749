package com.classmanage.service.impl;

import com.classmanage.common.exception.BusinessException;
import com.classmanage.entity.NoticeInfo;
import com.classmanage.entity.NoticeRead;
import com.classmanage.entity.User;
import com.classmanage.repository.NoticeReadRepository;
import com.classmanage.repository.NoticeRepository;
import com.classmanage.repository.UserRepository;
import com.classmanage.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    
    private final NoticeRepository noticeRepository;
    private final NoticeReadRepository noticeReadRepository;
    private final UserRepository userRepository;
    
    @Override
    public Page<NoticeInfo> getPage(Integer pageNum, Integer pageSize, Long classId, Integer noticeType, Integer status) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);
        return noticeRepository.findByConditions(classId, noticeType, status, pageRequest);
    }
    
    @Override
    public NoticeInfo getById(Long id) {
        return noticeRepository.findById(id)
            .orElseThrow(() -> new BusinessException("通知不存在"));
    }
    
    @Override
    public void create(NoticeInfo notice) {
        notice.setStatus(1);
        notice.setIsTop(0);
        notice.setPublishTime(LocalDateTime.now());
        notice.setCreateTime(LocalDateTime.now());
        noticeRepository.save(notice);
    }
    
    @Override
    public void update(NoticeInfo notice) {
        NoticeInfo existing = noticeRepository.findById(notice.getId())
            .orElseThrow(() -> new BusinessException("通知不存在"));
        
        existing.setTitle(notice.getTitle());
        existing.setContent(notice.getContent());
        existing.setNoticeType(notice.getNoticeType());
        existing.setClassId(notice.getClassId());
        noticeRepository.save(existing);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        noticeRepository.deleteById(id);
        noticeReadRepository.deleteByNoticeId(id);
    }

    @Override
    public void setTop(Long id, boolean isTop) {
        NoticeInfo notice = noticeRepository.findById(id)
            .orElseThrow(() -> new BusinessException("通知不存在"));
        notice.setIsTop(isTop ? 1 : 0);
        noticeRepository.save(notice);
    }
    
    @Override
    public void withdraw(Long id) {
        NoticeInfo notice = noticeRepository.findById(id)
            .orElseThrow(() -> new BusinessException("通知不存在"));
        notice.setStatus(0);
        noticeRepository.save(notice);
    }
    
    @Override
    public void markAsRead(Long noticeId, Long userId) {
        if (noticeReadRepository.findByNoticeIdAndUserId(noticeId, userId).isPresent()) {
            return;
        }
        
        NoticeRead read = new NoticeRead();
        read.setNoticeId(noticeId);
        read.setUserId(userId);
        read.setReadTime(LocalDateTime.now());
        noticeReadRepository.save(read);
    }
    
    @Override
    public List<Map<String, Object>> getReadStatus(Long noticeId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        NoticeInfo notice = noticeRepository.findById(noticeId).orElse(null);
        if (notice == null) {
            return result;
        }
        
        List<User> users;
        if (notice.getClassId() != null) {
            users = userRepository.findAll().stream()
                .filter(u -> notice.getClassId().equals(u.getClassId()) && u.getStatus() == 1)
                .toList();
        } else {
            users = userRepository.findAll().stream()
                .filter(u -> u.getStatus() == 1)
                .toList();
        }
        
        List<NoticeRead> reads = noticeReadRepository.findByNoticeId(noticeId);
        Set<Long> readUserIds = reads.stream()
            .map(NoticeRead::getUserId)
            .collect(Collectors.toSet());
        
        for (User user : users) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("username", user.getUsername());
            map.put("realName", user.getRealName());
            map.put("isRead", readUserIds.contains(user.getId()));
            result.add(map);
        }
        
        return result;
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return 0;
        }
        
        List<NoticeInfo> notices = noticeRepository.findVisibleNotices(user.getClassId());
        if (notices.isEmpty()) {
            return 0;
        }
        
        List<Long> noticeIds = notices.stream().map(NoticeInfo::getId).toList();
        long readCount = noticeReadRepository.countByUserIdAndNoticeIdIn(userId, noticeIds);
        
        return (int) (notices.size() - readCount);
    }
}
