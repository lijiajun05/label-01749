package com.classmanage.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色实体
 */
@Data
@Entity
@Table(name = "sys_role")
public class Role implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 角色名称 */
    @Column(name = "role_name")
    private String roleName;
    
    /** 角色编码 */
    @Column(name = "role_code")
    private String roleCode;
    
    /** 描述 */
    @Column(name = "description")
    private String description;
}
