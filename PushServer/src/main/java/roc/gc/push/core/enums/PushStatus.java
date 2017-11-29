package roc.gc.push.core.enums;

public enum PushStatus {
    TO_PUSH(0, "等待推送"), PUSHING(1, "推送中"), PUSH_SUCCESS(2, "推送成功"), PUSH_FAILED(3, "推送失败");
    public int status;

    public String desc;

    private PushStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
