package com.classmanage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩实体
 */
@Data
@Entity
@Table(name = "grade_info")
@EntityListeners(AuditingEntityListener.class)
public class GradeInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 学生ID */
    @Column(name = "student_id")
    private Long studentId;
    
    /** 课程ID */
    @Column(name = "course_id")
    private Long courseId;
    
    /** 类型(1平时/2期中/3期末) */
    @Column(name = "grade_type")
    private Integer gradeType;
    
    /** 分数 */
    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;
    
    /** 学期 */
    @Column(name = "semester")
    private String semester;
    
    /** 创建时间 */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    /** 创建人 */
    @Column(name = "create_by")
    private Long createBy;
    
    /** 学生姓名 - 非数据库字段 */
    @Transient
    private String studentName;
    
    /** 学号 - 非数据库字段 */
    @Transient
    private String studentNo;
    
    /** 课程名称 - 非数据库字段 */
    @Transient
    private String courseName;
}
