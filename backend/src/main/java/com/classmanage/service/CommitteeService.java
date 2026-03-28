package com.classmanage.service;

import com.classmanage.entity.ClassCommittee;
import java.util.List;
import java.util.Map;

public interface CommitteeService {
    
    List<Map<String, Object>> getByClassId(Long classId);
    
    List<Map<String, Object>> getAll();
    
    void create(ClassCommittee committee);
    
    void update(ClassCommittee committee);
    
    void delete(Long id);
}
