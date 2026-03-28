package com.classmanage.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 班委信息实体
 */
@Data
@Entity
@Table(name = "class_committee")
public class ClassCommittee implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 班级ID */
    @Column(name = "class_id")
    private Long classId;
    
    /** 学生ID */
    @Column(name = "student_id")
    private Long studentId;
    
    /** 职务 */
    @Column(name = "position")
    private String position;
    
    /** 任职开始日期 */
    @Column(name = "start_date")
    private LocalDate startDate;
    
    /** 任职结束日期 */
    @Column(name = "end_date")
    private LocalDate endDate;
    
    /** 状态 */
    @Column(name = "status")
    private Integer status;
    
    /** 学生姓名 - 非数据库字段 */
    @Transient
    private String studentName;
    
    /** 学号 - 非数据库字段 */
    @Transient
    private String studentNo;
}
