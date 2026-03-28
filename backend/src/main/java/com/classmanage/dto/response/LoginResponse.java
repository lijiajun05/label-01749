package com.classmanage.dto.response;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginResponse {
    
    /** JWT Token */
    private String token;
    
    /** 用户ID */
    private Long userId;
    
    /** 用户名 */
    private String username;
    
    /** 真实姓名 */
    private String realName;
    
    /** 角色编码 */
    private String roleCode;
    
    /** 角色名称 */
    private String roleName;
    
    /** 头像 */
    private String avatar;
}
