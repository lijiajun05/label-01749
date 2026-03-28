package com.classmanage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤记录实体
 */
@Data
@Entity
@Table(name = "attendance_record")
@EntityListeners(AuditingEntityListener.class)
public class AttendanceRecord implements Serializable {
    
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
    
    /** 考勤日期 */
    @Column(name = "attendance_date")
    private LocalDate attendanceDate;
    
    /** 状态(0缺勤/1出勤/2迟到/3早退/4请假) */
    @Column(name = "status")
    private Integer status;
    
    /** 备注 */
    @Column(name = "remark")
    private String remark;
    
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
