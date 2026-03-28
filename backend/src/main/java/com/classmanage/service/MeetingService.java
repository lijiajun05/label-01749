package com.classmanage.service;

import com.classmanage.entity.ClassMeeting;
import org.springframework.data.domain.Page;

public interface MeetingService {
    
    Page<ClassMeeting> getPage(Long classId, Integer pageNum, Integer pageSize);
    
    ClassMeeting getById(Long id);
    
    void create(ClassMeeting meeting);
    
    void update(ClassMeeting meeting);
    
    void delete(Long id);
}
