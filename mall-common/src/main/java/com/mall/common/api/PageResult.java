package com.mall.common.api;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应结构，配合 {@link Result} 使用：{@code Result<PageResult<T>>}。
 *
 * <p>页码从 1 开始，{@code pages} 为总页数（由 total 与 pageSize 计算得出，前端无需自己算）。
 *
 * @param <T> 列表元素类型
 */
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 当前页数据，永不为 null，无数据时为空集合 */
    private List<T> records;

    /** 总记录数 */
    private long total;

    /** 当前页码，从 1 开始 */
    private long pageNum;

    /** 每页条数 */
    private long pageSize;

    /** 总页数 = ceil(total / pageSize) */
    private long pages;

    /** 供 Jackson 反序列化使用 */
    public PageResult() {
    }

    /**
     * 构造分页结果，自动计算总页数。
     *
     * @param records  当前页数据，传 null 会被转成空集合
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页条数，小于等于 0 时总页数记为 0
     */
    public PageResult(List<T> records, long total, long pageNum, long pageSize) {
        this.records = records == null ? Collections.emptyList() : records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pageSize <= 0 ? 0 : (total + pageSize - 1) / pageSize;
    }

    /**
     * 空分页结果，用于查询无数据时避免返回 null。
     */
    public static <T> PageResult<T> empty(long pageNum, long pageSize) {
        return new PageResult<>(Collections.emptyList(), 0, pageNum, pageSize);
    }

    /**
     * 快捷构造方法，等价于 {@code new PageResult<>(records, total, pageNum, pageSize)}。
     */
    public static <T> PageResult<T> of(List<T> records, long total, long pageNum, long pageSize) {
        return new PageResult<>(records, total, pageNum, pageSize);
    }

    // ---------------------------------------------------------------- getter / setter

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }
}
