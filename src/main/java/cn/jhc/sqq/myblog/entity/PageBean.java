package cn.jhc.sqq.myblog.entity;

/**
 * @author sqq
 * @ 2019/8/22 15:19
 */
public class PageBean {

//    当前页,1开始
    private int pageNumber;
//   页面数据条数
    private int pageSize;

//    第几条开始
    private int start;

    public PageBean(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStart() {
        return (this.pageNumber-1)*this.pageSize;
    }

}
