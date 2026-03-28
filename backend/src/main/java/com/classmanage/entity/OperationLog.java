package com.classmanage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@Entity
@Table(name = "operation_log")
@EntityListeners(AuditingEntityListener.class)
public class OperationLog implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 操作用户ID */
    @Column(name = "user_id")
    private Long userId;
    
    /** 用户名 */
    @Column(name = "username")
    private String username;
    
    /** 操作类型 */
    @Column(name = "operation_type")
    private String operationType;
    
    /** 操作模块 */
    @Column(name = "module")
    private String module;
    
    /** 操作内容 */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    /** IP地址 */
    @Column(name = "ip_address")
    private String ipAddress;
    
    /** 创建时间 */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
}
