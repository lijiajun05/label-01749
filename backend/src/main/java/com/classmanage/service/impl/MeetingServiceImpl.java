package com.classmanage.service.impl;

import com.classmanage.common.exception.BusinessException;
import com.classmanage.entity.ClassMeeting;
import com.classmanage.repository.ClassRepository;
import com.classmanage.repository.MeetingRepository;
import com.classmanage.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {
    
    private final MeetingRepository meetingRepository;
    private final ClassRepository classRepository;
    
    @Override
    public Page<ClassMeeting> getPage(Long classId, Integer pageNum, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);
        Page<ClassMeeting> result = meetingRepository.findByConditions(classId, pageRequest);
        
        if (!result.getContent().isEmpty()) {
            Map<Long, String> classNameMap = classRepository.findAll().stream()
                .collect(Collectors.toMap(c -> c.getId(), c -> c.getClassName()));
            result.getContent().forEach(meeting -> {
                meeting.setClassName(classNameMap.get(meeting.getClassId()));
            });
        }
        return result;
    }
    
    @Override
    public ClassMeeting getById(Long id) {
        return meetingRepository.findById(id)
            .orElseThrow(() -> new BusinessException("班会记录不存在"));
    }
    
    @Override
    public void create(ClassMeeting meeting) {
        meeting.setCreateTime(LocalDateTime.now());
        meetingRepository.save(meeting);
    }
    
    @Override
    public void update(ClassMeeting meeting) {
        ClassMeeting existing = meetingRepository.findById(meeting.getId())
            .orElseThrow(() -> new BusinessException("班会记录不存在"));

        existing.setTitle(meeting.getTitle());
        existing.setContent(meeting.getContent());
        existing.setMeetingTime(meeting.getMeetingTime());
        existing.setLocation(meeting.getLocation());
        if (meeting.getClassId() != null) {
            existing.setClassId(meeting.getClassId());
        }
        meetingRepository.save(existing);
    }

    
    @Override
    public void delete(Long id) {
        meetingRepository.deleteById(id);
    }
}
