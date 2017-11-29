package roc.gc.push.pojo;

import java.util.Date;

import roc.gc.push.core.base.Page;

/**
 * 
 * @ClassName: PushMessage
 * @Description: 推送的消息实体(具体表示一次推送)
 * @author guocan
 * @date 2017年8月29日 下午2:27:08
 *
 */
public class Message extends Page {

    private Long id;// 消息主键

    private Date pushTime;// 推送时间

    private Date createTime;// 消息创建时间

    private String sendUserToken;// 发送者

    private String acceptorUserToken;// 接收者

    private String body;// 消息体(json格式)

    private int status;// 推送状态(0:等待推送 1：推送中 2:推送成功 3：推送失败)

    public Message() {
        this.createTime = new Date();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getPushTime() {
        return pushTime;
    }

    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }

    public String getSendUserToken() {
        return sendUserToken;
    }

    public void setSendUserToken(String sendUserToken) {
        this.sendUserToken = sendUserToken;
    }

    public String getAcceptorUserToken() {
        return acceptorUserToken;
    }

    public void setAcceptorUserToken(String acceptorUserToken) {
        this.acceptorUserToken = acceptorUserToken;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
