package com.classmanage.common.enums;

import lombok.Getter;

/**
 * 通知类型枚举
 */
@Getter
public enum NoticeType {
    
    PAYMENT(1, "缴费通知"),
    AWARD(2, "评优通知"),
    ACTIVITY(3, "活动通知"),
    OTHER(4, "其他通知");
    
    private final int code;
    private final String desc;
    
    NoticeType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static NoticeType fromCode(int code) {
        for (NoticeType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
