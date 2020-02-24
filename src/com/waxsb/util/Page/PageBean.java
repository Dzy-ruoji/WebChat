package com.waxsb.util.Page;
/*
* 分页对象
*
* */
import java.util.List;

public class PageBean<T> {
    private int totalCount;//总记录数
    private int totalPage;//总页码
    private List<T> list;
    private int currentPage;//当前页码
    private int rows;//每页显示的记录数

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageBean{" +
                "totalCount=" + totalCount +
                ", totalPage=" + totalPage +
                ", list=" + list +
                ", currentPage=" + currentPage +
                ", rows=" + rows +
                '}';
    }

    /** 判断当前是否是第一页 */
    public boolean getIsFirst(){
        return  currentPage==1;
    }
    /** 判断当前是否是最后一页 */
    public boolean getIsLast(){
        return  currentPage==totalPage;
    }
    /** 上一页 */
    public Integer getPrevPage(){
        return  currentPage-1;
    }
    /** 下一页 */
    public Integer getNextPage(){
        return  currentPage+1;
    }

}
