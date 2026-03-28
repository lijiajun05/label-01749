package com.classmanage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生实体
 */
@Data
@Entity
@Table(name = "student_info")
@EntityListeners(AuditingEntityListener.class)
public class StudentInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 学号 */
    @Column(name = "student_no", unique = true)
    private String studentNo;
    
    /** 关联用户ID */
    @Column(name = "user_id")
    private Long userId;
    
    /** 班级ID */
    @Column(name = "class_id")
    private Long classId;
    
    /** 性别 */
    @Column(name = "gender")
    private String gender;
    
    /** 入学日期 */
    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;
    
    /** 创建时间 */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    /** 学生姓名 - 非数据库字段 */
    @Transient
    private String studentName;
    
    /** 班级名称 - 非数据库字段 */
    @Transient
    private String className;
    
    /** 手机号 - 非数据库字段 */
    @Transient
    private String phone;
    
    /** 邮箱 - 非数据库字段 */
    @Transient
    private String email;
}
