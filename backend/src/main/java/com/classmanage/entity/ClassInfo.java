package com.classmanage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 班级实体
 */
@Data
@Entity
@Table(name = "class_info")
@EntityListeners(AuditingEntityListener.class)
public class ClassInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 班级名称 */
    @Column(name = "class_name")
    private String className;
    
    /** 年级 */
    @Column(name = "grade")
    private String grade;
    
    /** 专业 */
    @Column(name = "major")
    private String major;
    
    /** 院系 */
    @Column(name = "department")
    private String department;
    
    /** 学生人数 */
    @Column(name = "student_count")
    private Integer studentCount;
    
    /** 班主任ID */
    @Column(name = "teacher_id")
    private Long teacherId;
    
    /** 状态 */
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
    
    /** 班主任姓名 - 非数据库字段 */
    @Transient
    private String teacherName;
}
