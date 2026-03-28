package com.classmanage.dto.response;

import lombok.Data;

/**
 * 用户信息响应
 */
@Data
public class UserInfoResponse {
    
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private String roleCode;
    private String roleName;
    private Long classId;
    private String className;
}
