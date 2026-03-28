package com.classmanage.common.constant;

/**
 * 系统常量
 */
public class Constants {
    
    private Constants() {}
    
    /** JWT Token 前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";
    
    /** JWT Token Header */
    public static final String TOKEN_HEADER = "Authorization";
    
    /** 默认密码 */
    public static final String DEFAULT_PASSWORD = "123456";
    
    /** 状态 - 启用 */
    public static final int STATUS_ENABLE = 1;
    
    /** 状态 - 禁用 */
    public static final int STATUS_DISABLE = 0;
    
    /** 分页默认页码 */
    public static final int DEFAULT_PAGE_NUM = 1;
    
    /** 分页默认大小 */
    public static final int DEFAULT_PAGE_SIZE = 10;
    
    /** 预警阈值 - 连续未交作业次数 */
    public static final int WARNING_ASSIGNMENT_MISS_THRESHOLD = 3;
    
    /** 预警阈值 - 出勤率 */
    public static final double WARNING_ATTENDANCE_RATE_THRESHOLD = 0.8;
    
    /** 预警阈值 - 挂科数量 */
    public static final int WARNING_FAIL_COURSE_THRESHOLD = 2;
    
    /** 及格分数线 */
    public static final double PASS_SCORE = 60.0;
}
