package com.classmanage.common.result;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果
 */
@Data
public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 总记录数 */
    private long total;
    
    /** 当前页数据 */
    private List<T> records;
    
    /** 当前页码 */
    private long current;
    
    /** 每页大小 */
    private long size;
    
    /** 总页数 */
    private long pages;
    
    public PageResult() {}
    
    public PageResult(long total, List<T> records, long current, long size) {
        this.total = total;
        this.records = records;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size;
    }
    
    public static <T> PageResult<T> of(long total, List<T> records, long current, long size) {
        return new PageResult<>(total, records, current, size);
    }
    
    /**
     * 从 Spring Data Page 对象创建 PageResult
     */
    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(
            page.getTotalElements(), 
            page.getContent(), 
            page.getNumber() + 1,  // Spring Data页码从0开始，转换为从1开始
            page.getSize()
        );
    }
}
