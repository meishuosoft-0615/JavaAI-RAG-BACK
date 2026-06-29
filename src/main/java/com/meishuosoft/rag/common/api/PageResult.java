package com.meishuosoft.rag.common.api;

import java.util.List;
import java.util.Collections;

/**
 * 分页结果包装。
 */
public class PageResult<T> {

    /** 符合条件的总记录数。 */
    private final long total;

    /** 当前页数据列表。 */
    private final List<T> items;

    public PageResult(long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public static <T> PageResult<T> of(long total, List<T> items) {
        return new PageResult<>(total, items == null ? Collections.emptyList() : items);
    }

    public long getTotal() {
        return total;
    }

    public List<T> getItems() {
        return items;
    }
}
