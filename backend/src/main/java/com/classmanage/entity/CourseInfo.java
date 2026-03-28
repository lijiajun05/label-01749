package com.classmanage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程实体
 */
@Data
@Entity
@Table(name = "course_info")
@EntityListeners(AuditingEntityListener.class)
public class CourseInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 课程名称 */
    @Column(name = "course_name")
    private String courseName;
    
    /** 课程代码 */
    @Column(name = "course_code")
    private String courseCode;
    
    /** 任课教师ID */
    @Column(name = "teacher_id")
    private Long teacherId;
    
    /** 学分 */
    @Column(name = "credit")
    private Integer credit;
    
    /** 学期 */
    @Column(name = "semester")
    private String semester;
    
    /** 状态 */
    @Column(name = "status")
    private Integer status;
    
    /** 创建时间 */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    /** 教师姓名 - 非数据库字段 */
    @Transient
    private String teacherName;
}
