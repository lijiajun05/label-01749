package com.classmanage.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 作业提交实体
 */
@Data
@Entity
@Table(name = "assignment_submit")
public class AssignmentSubmit implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 作业ID */
    @Column(name = "assignment_id")
    private Long assignmentId;
    
    /** 学生ID */
    @Column(name = "student_id")
    private Long studentId;
    
    /** 状态(0未提交/1已提交/2逾期提交) */
    @Column(name = "status")
    private Integer status;
    
    /** 提交时间 */
    @Column(name = "submit_time")
    private LocalDateTime submitTime;
    
    /** 备注 */
    @Column(name = "remark")
    private String remark;
    
    /** 学生姓名 - 非数据库字段 */
    @Transient
    private String studentName;
    
    /** 学号 - 非数据库字段 */
    @Transient
    private String studentNo;
}
