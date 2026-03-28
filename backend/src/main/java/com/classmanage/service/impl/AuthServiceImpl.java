package com.classmanage.service.impl;

import com.classmanage.common.exception.BusinessException;
import com.classmanage.common.utils.JwtUtils;
import com.classmanage.common.utils.SecurityUtils;
import com.classmanage.dto.request.LoginRequest;
import com.classmanage.dto.response.LoginResponse;
import com.classmanage.dto.response.UserInfoResponse;
import com.classmanage.entity.ClassInfo;
import com.classmanage.entity.Role;
import com.classmanage.entity.User;
import com.classmanage.repository.ClassRepository;
import com.classmanage.repository.RoleRepository;
import com.classmanage.repository.UserRepository;
import com.classmanage.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClassRepository classRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw new BusinessException(401, "账号已被禁用");
        }
        
        // 获取角色信息
        String roleCode = null;
        String roleName = null;
        if (user.getRoleId() != null) {
            Role role = roleRepository.findById(user.getRoleId()).orElse(null);
            if (role != null) {
                roleCode = role.getRoleCode();
                roleName = role.getRoleName();
            }
        }
        
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), roleCode);
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRoleCode(roleCode);
        response.setRoleName(roleName);
        response.setAvatar(user.getAvatar());
        
        log.info("用户登录成功: {}", user.getUsername());
        return response;
    }

    @Override
    public UserInfoResponse getCurrentUserInfo() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(401, "用户不存在"));
        
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setClassId(user.getClassId());
        
        if (user.getRoleId() != null) {
            roleRepository.findById(user.getRoleId()).ifPresent(role -> {
                response.setRoleCode(role.getRoleCode());
                response.setRoleName(role.getRoleName());
            });
        }
        
        if (user.getClassId() != null) {
            classRepository.findById(user.getClassId()).ifPresent(classInfo -> {
                response.setClassName(classInfo.getClassName());
            });
        }
        
        return response;
    }
    
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("用户修改密码成功: {}", user.getUsername());
    }
    
    @Override
    public String getPublicKey() {
        return null;
    }
}
