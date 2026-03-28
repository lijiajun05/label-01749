package com.classmanage.controller;

import com.classmanage.annotation.OperationLog;
import com.classmanage.common.result.Result;
import com.classmanage.dto.request.LoginRequest;
import com.classmanage.dto.response.LoginResponse;
import com.classmanage.dto.response.UserInfoResponse;
import com.classmanage.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 获取RSA公钥
     */
    @GetMapping("/public-key")
    public Result<String> getPublicKey() {
        return Result.success(authService.getPublicKey());
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @OperationLog(module = "认证管理", type = "登录", description = "用户登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @OperationLog(module = "认证管理", type = "登出", description = "用户登出")
    public Result<Void> logout() {
        return Result.success();
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoResponse> getCurrentUserInfo() {
        return Result.success(authService.getCurrentUserInfo());
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    @OperationLog(module = "认证管理", type = "修改", description = "修改密码")
    public Result<Void> changePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        authService.changePassword(oldPassword, newPassword);
        return Result.success();
    }
}
