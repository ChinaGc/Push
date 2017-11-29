package roc.gc.push.core.base;

public class Page {

    private int pageId = 1; // 当前页

    private int pageSize = 10; // 页大小

    private int pageOffset = 0;// 查询起始页

    private String orderField;// 排序字段

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageOffset() {
        this.pageOffset = (this.pageId - 1) * this.pageSize;
        return pageOffset;
    }

    public void setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
    }

}
