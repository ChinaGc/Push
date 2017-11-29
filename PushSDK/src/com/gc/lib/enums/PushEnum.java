package com.gc.lib.enums;

public enum PushEnum {

    SUCCESS(200, "成功"), ERROR(400, "客户端网络未连接"), CLIENTFAILED(100, "客户端未处理");

    PushEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    private int status;

    private String desc;

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
