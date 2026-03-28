package com.classmanage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@Entity
@Table(name = "sys_user")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    
    /** 密码 */
    @Column(name = "password")
    private String password;
    
    /** 真实姓名 */
    @NotBlank(message = "真实姓名不能为空")
    @Column(name = "real_name")
    private String realName;
    
    /** 手机号 */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Column(name = "phone")
    private String phone;
    
    /** 邮箱 */
    @Email(message = "邮箱格式不正确")
    @Column(name = "email")
    private String email;
    
    /** 头像URL */
    @Column(name = "avatar")
    private String avatar;
    
    /** 角色ID */
    @Column(name = "role_id")
    private Integer roleId;
    
    /** 所属班级ID */
    @Column(name = "class_id")
    private Long classId;
    
    /** 状态(0禁用/1启用) */
    @Column(name = "status")
    private Integer status;
    
    /** 创建时间 */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @LastModifiedDate
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    /** 角色编码 - 非数据库字段 */
    @Transient
    private String roleCode;
    
    /** 角色名称 - 非数据库字段 */
    @Transient
    private String roleName;
    
    /** 班级名称 - 非数据库字段 */
    @Transient
    private String className;
}
