package cn.ocoop.framework.common.util;

import com.github.pagehelper.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

@Data
public class Paging<T> implements Serializable, Iterable<T> {
    private long totalRow;
    private long pageSize = 10;
    private long totalPage;
    private long currentPage = 1;
    private Collection<T> data;

    public Paging() {
    }


    public static <T> Paging<T> build(int currentPage, int pageSize, long total, List<T> data) {
        Paging<T> paging = new Paging<>();
        paging.setCurrentPage(currentPage);
        paging.setPageSize(pageSize);
        paging.setTotalRow(total);
        paging.setData(data);
        return paging;
    }

    public static <T> Paging<T> build(int currentPage, int pageSize, List<T> data) {
        Paging<T> paging = new Paging<>();
        paging.setCurrentPage(currentPage);
        paging.setPageSize(pageSize);
        if (data instanceof Page) {
            paging.setTotalRow(((Page) data).getTotal());
        }
        paging.setData(data);
        return paging;
    }

    public static <T> Paging<T> build(int currentPage, int pageSize, long totalRow) {
        Paging<T> paging = new Paging<>();
        paging.setCurrentPage(currentPage);
        paging.setPageSize(pageSize);
        paging.setTotalRow(totalRow);
        return paging;
    }

    public Paging setTotalRow(long totalRow) {
        this.totalRow = totalRow;
        this.setTotalPage(this.totalRow / getPageSize() + (this.totalRow % getPageSize() != 0 ? 1 : 0));
//        if (getCurrentPage() > getTotalPage()) {
//            setCurrentPage(getTotalPage());
//        } else if (getCurrentPage() < 1) {
//            setCurrentPage(1);
//        }
        return this;
    }

    public long getStart() {
        return (getCurrentPage() - 1) * getPageSize();
    }

    public long getEnd() {
        return getStart() + getPageSize();
    }

    public Paging setData(Collection<T> data) {
        this.data = data;
        return this;
    }


    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        data.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return data.spliterator();
    }

}