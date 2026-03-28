package com.classmanage.common.enums;

import lombok.Getter;

/**
 * 预警类型枚举
 */
@Getter
public enum WarningType {
    
    ASSIGNMENT_MISS(1, "作业未交"),
    LOW_ATTENDANCE(2, "出勤率低"),
    FAIL_RISK(3, "挂科风险");
    
    private final int code;
    private final String desc;
    
    WarningType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static WarningType fromCode(int code) {
        for (WarningType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
