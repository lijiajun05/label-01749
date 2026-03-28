package com.classmanage.common.enums;

import lombok.Getter;

/**
 * 作业提交状态枚举
 */
@Getter
public enum SubmitStatus {
    
    NOT_SUBMITTED(0, "未提交"),
    SUBMITTED(1, "已提交"),
    LATE_SUBMITTED(2, "逾期提交");
    
    private final int code;
    private final String desc;
    
    SubmitStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static SubmitStatus fromCode(int code) {
        for (SubmitStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
