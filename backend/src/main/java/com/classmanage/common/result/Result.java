package com.classmanage.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 */
@Data
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 状态码 */
    private int code;
    
    /** 提示信息 */
    private String message;
    
    /** 数据 */
    private T data;
    
    public Result() {}
    
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }
    
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
    
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
    
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权，请先登录", null);
    }
    
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "无权限访问", null);
    }
    
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null);
    }
}
