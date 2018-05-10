package com.zzhl.dto.requst;

public class Page {
    Integer pageNumber;
    Integer pageSize;

    public Integer getPageNumber() {
        return pageNumber == null ? 1 : pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize == null ? 20 : pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffset() {
        return (getPageNumber() - 1) * getPageSize();
    }

    public Integer getLimit() {
        return getPageSize();
    }
}
