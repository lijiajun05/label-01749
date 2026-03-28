package com.classmanage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预警记录实体
 */
@Data
@Entity
@Table(name = "warning_record")
@EntityListeners(AuditingEntityListener.class)
public class WarningRecord implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 学生ID */
    @Column(name = "student_id")
    private Long studentId;
    
    /** 类型(1作业未交/2出勤率低/3挂科风险) */
    @Column(name = "warning_type")
    private Integer warningType;
    
    /** 预警标题 */
    @Column(name = "title")
    private String title;
    
    /** 预警内容 */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    /** 状态(0未处理/1已处理/2已忽略) */
    @Column(name = "status")
    private Integer status;
    
    /** 处理人ID */
    @Column(name = "handler_id")
    private Long handlerId;
    
    /** 处理结果 */
    @Column(name = "handle_result", columnDefinition = "TEXT")
    private String handleResult;
    
    /** 处理时间 */
    @Column(name = "handle_time")
    private LocalDateTime handleTime;
    
    /** 创建时间 */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    /** 学生姓名 - 非数据库字段 */
    @Transient
    private String studentName;
    
    /** 学号 - 非数据库字段 */
    @Transient
    private String studentNo;
    
    /** 班级名称 - 非数据库字段 */
    @Transient
    private String className;
}
