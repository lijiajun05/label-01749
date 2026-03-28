package com.classmanage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 班会记录实体
 */
@Data
@Entity
@Table(name = "class_meeting")
@EntityListeners(AuditingEntityListener.class)
public class ClassMeeting implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 班级ID */
    @Column(name = "class_id")
    private Long classId;
    
    /** 班会主题 */
    @Column(name = "title")
    private String title;
    
    /** 班会内容 */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    /** 班会时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "meeting_time")
    private LocalDateTime meetingTime;
    
    /** 地点 */
    @Column(name = "location")
    private String location;
    
    /** 记录人ID */
    @Column(name = "recorder_id")
    private Long recorderId;
    
    /** 创建时间 */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    /** 记录人姓名 - 非数据库字段 */
    @Transient
    private String recorderName;
    
    /** 班级名称 - 非数据库字段 */
    @Transient
    private String className;
}
