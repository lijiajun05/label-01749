package com.classmanage.common.utils;

import com.classmanage.security.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 安全工具类
 */
public class SecurityUtils {
    
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    
    private SecurityUtils() {}
    
    /**
     * 获取当前登录用户
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        }
        return null;
    }
    
    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUserId() : null;
    }
    
    /**
     * 获取当前用户ID (别名方法)
     */
    public static Long getCurrentUserId() {
        return getUserId();
    }
    
    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUsername() : null;
    }
    
    /**
     * 获取当前用户角色编码
     */
    public static String getRoleCode() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getRoleCode() : null;
    }
    
    /**
     * 获取当前用户班级ID
     */
    public static Long getClassId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getClassId() : null;
    }
    
    /**
     * 密码加密
     */
    public static String encryptPassword(String password) {
        return PASSWORD_ENCODER.encode(password);
    }
    
    /**
     * 密码匹配
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }
}
