package com.classmanage.security;

import com.classmanage.entity.Role;
import com.classmanage.entity.User;
import com.classmanage.repository.RoleRepository;
import com.classmanage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("用户不存在: {}", username);
                    return new UsernameNotFoundException("用户不存在: " + username);
                });
        
        // 查询角色编码
        String roleCode = null;
        if (user.getRoleId() != null) {
            roleCode = roleRepository.findById(user.getRoleId())
                    .map(Role::getRoleCode)
                    .orElse(null);
        }
        
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());
        loginUser.setRealName(user.getRealName());
        loginUser.setRoleCode(roleCode);
        loginUser.setClassId(user.getClassId());
        loginUser.setStatus(user.getStatus());
        
        return loginUser;
    }
}
