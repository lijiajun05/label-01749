package com.classmanage.service;

import com.classmanage.dto.request.LoginRequest;
import com.classmanage.dto.response.LoginResponse;
import com.classmanage.dto.response.UserInfoResponse;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 获取当前用户信息
     */
    UserInfoResponse getCurrentUserInfo();
    
    /**
     * 修改密码
     */
    void changePassword(String oldPassword, String newPassword);
    
    /**
     * 获取RSA公钥
     */
    String getPublicKey();
}
