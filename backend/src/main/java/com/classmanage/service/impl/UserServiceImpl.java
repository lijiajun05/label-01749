package com.classmanage.service.impl;

import com.classmanage.common.exception.BusinessException;
import com.classmanage.entity.User;
import com.classmanage.repository.UserRepository;
import com.classmanage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final String DEFAULT_PASSWORD = "123456";
    
    @Override
    public Page<User> getPage(Integer pageNum, Integer pageSize, String username, String realName, Integer roleId) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        return userRepository.findByConditions(
            StringUtils.hasText(username) ? username : null,
            StringUtils.hasText(realName) ? realName : null,
            roleId,
            pageRequest
        );
    }
    
    @Override
    public User getById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        user.setPassword(null);
        return user;
    }
    
    @Override
    public void create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void update(User user) {
        User existing = userRepository.findById(user.getId())
            .orElseThrow(() -> new BusinessException("用户不存在"));
        
        if (!existing.getUsername().equals(user.getUsername()) 
            && userRepository.existsByUsernameAndIdNot(user.getUsername(), user.getId())) {
            throw new BusinessException("用户名已存在");
        }
        
        existing.setUsername(user.getUsername());
        existing.setRealName(user.getRealName());
        existing.setPhone(user.getPhone());
        existing.setEmail(user.getEmail());
        existing.setRoleId(user.getRoleId());
        existing.setClassId(user.getClassId());
        existing.setUpdateTime(LocalDateTime.now());
        userRepository.save(existing);
    }
    
    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("用户不存在");
        }
        userRepository.deleteById(id);
    }
    
    @Override
    public void updateStatus(Long id, Integer status) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    public void resetPassword(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }
}
