package com.classmanage.service;

import com.classmanage.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    
    Page<User> getPage(Integer pageNum, Integer pageSize, String username, String realName, Integer roleId);
    
    User getById(Long id);
    
    void create(User user);
    
    void update(User user);
    
    void delete(Long id);
    
    void updateStatus(Long id, Integer status);
    
    void resetPassword(Long id);
}
