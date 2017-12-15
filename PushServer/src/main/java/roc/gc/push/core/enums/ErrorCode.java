package roc.gc.push.core.enums;

public enum ErrorCode {

    USER_NOT_EXIST(404, "用户不存在"), LOGIN_ON_ONTHER(401, "用户已在其他设备上登录"), SERVER_ERROR(500, "服务器错误");
    public int code;

    public String desc;

    private ErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
