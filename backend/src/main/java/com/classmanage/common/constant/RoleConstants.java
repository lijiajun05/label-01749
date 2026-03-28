package com.classmanage.common.constant;

/**
 * 角色常量
 */
public class RoleConstants {
    
    private RoleConstants() {}
    
    /** 系统管理员 */
    public static final String ADMIN = "ADMIN";
    
    /** 班主任 */
    public static final String CLASS_TEACHER = "CLASS_TEACHER";
    
    /** 任课教师 */
    public static final String COURSE_TEACHER = "COURSE_TEACHER";
    
    /** 学生 */
    public static final String STUDENT = "STUDENT";
    
    /** 角色ID - 管理员 */
    public static final int ROLE_ID_ADMIN = 1;
    
    /** 角色ID - 班主任 */
    public static final int ROLE_ID_CLASS_TEACHER = 2;
    
    /** 角色ID - 任课教师 */
    public static final int ROLE_ID_COURSE_TEACHER = 3;
    
    /** 角色ID - 学生 */
    public static final int ROLE_ID_STUDENT = 4;
}
