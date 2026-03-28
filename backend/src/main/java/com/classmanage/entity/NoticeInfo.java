package com.classmanage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知公告实体
 */
@Data
@Entity
@Table(name = "notice_info")
@EntityListeners(AuditingEntityListener.class)
public class NoticeInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 标题 */
    @Column(name = "title")
    private String title;
    
    /** 内容 */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    /** 类型(1缴费/2评优/3活动/4其他) */
    @Column(name = "notice_type")
    private Integer noticeType;
    
    /** 发布人ID */
    @Column(name = "publisher_id")
    private Long publisherId;
    
    /** 目标班级ID */
    @Column(name = "class_id")
    private Long classId;
    
    /** 是否置顶 */
    @Column(name = "is_top")
    private Integer isTop;
    
    /** 状态(0撤回/1发布) */
    @Column(name = "status")
    private Integer status;
    
    /** 发布时间 */
    @Column(name = "publish_time")
    private LocalDateTime publishTime;
    
    /** 创建时间 */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    /** 发布人姓名 - 非数据库字段 */
    @Transient
    private String publisherName;
    
    /** 班级名称 - 非数据库字段 */
    @Transient
    private String className;
    
    /** 是否已读 - 非数据库字段 */
    @Transient
    private Boolean isRead;
}
