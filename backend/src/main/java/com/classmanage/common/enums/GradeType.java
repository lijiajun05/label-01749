package com.classmanage.common.enums;

import lombok.Getter;

/**
 * 成绩类型枚举
 */
@Getter
public enum GradeType {
    
    REGULAR(1, "平时成绩"),
    MIDTERM(2, "期中成绩"),
    FINAL(3, "期末成绩");
    
    private final int code;
    private final String desc;
    
    GradeType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static GradeType fromCode(int code) {
        for (GradeType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
