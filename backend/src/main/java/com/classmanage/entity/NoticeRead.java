package com.classmanage.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知阅读记录实体
 */
@Data
@Entity
@Table(name = "notice_read")
public class NoticeRead implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 通知ID */
    @Column(name = "notice_id")
    private Long noticeId;
    
    /** 用户ID */
    @Column(name = "user_id")
    private Long userId;
    
    /** 阅读时间 */
    @Column(name = "read_time")
    private LocalDateTime readTime;
}
