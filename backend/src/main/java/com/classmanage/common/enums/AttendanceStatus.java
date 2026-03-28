package com.classmanage.common.enums;

import lombok.Getter;

/**
 * 考勤状态枚举
 */
@Getter
public enum AttendanceStatus {
    
    ABSENT(0, "缺勤"),
    PRESENT(1, "出勤"),
    LATE(2, "迟到"),
    EARLY_LEAVE(3, "早退"),
    LEAVE(4, "请假");
    
    private final int code;
    private final String desc;
    
    AttendanceStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static AttendanceStatus fromCode(int code) {
        for (AttendanceStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
