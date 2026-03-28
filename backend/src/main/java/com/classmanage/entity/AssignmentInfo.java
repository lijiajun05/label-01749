package com.classmanage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 作业实体
 */
@Data
@Entity
@Table(name = "assignment_info")
@EntityListeners(AuditingEntityListener.class)
public class AssignmentInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 课程ID */
    @Column(name = "course_id")
    private Long courseId;
    
    /** 作业标题 */
    @Column(name = "title")
    private String title;
    
    /** 作业描述 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /** 截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "deadline")
    private LocalDateTime deadline;
    
    /** 状态(0关闭/1开放) */
    @Column(name = "status")
    private Integer status;
    
    /** 创建时间 */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    /** 创建人 */
    @Column(name = "create_by")
    private Long createBy;
    
    /** 课程名称 - 非数据库字段 */
    @Transient
    private String courseName;
    
    /** 提交数量 - 非数据库字段 */
    @Transient
    private Integer submitCount;
    
    /** 总学生数 - 非数据库字段 */
    @Transient
    private Integer totalCount;
}
