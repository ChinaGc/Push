package roc.gc.push.pojo;

import java.util.Date;

import roc.gc.push.core.base.Page;

/**
 * 
 * @ClassName: PushUser
 * @Description: 推送用户
 * @author guocan
 * @date 2017年8月29日 下午2:23:11
 *
 */
public class User extends Page {

    private Long id; // 主键(自增长)

    private String token;// 连接服务器令牌

    private Date registTime;// 注册时间

    private Date createTime;// 创建时间

    private Date lastConnectTime;// 最后连接时间

    public User() {
        this.createTime = new Date();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getRegistTime() {
        return registTime;
    }

    public void setRegistTime(Date registTime) {
        this.registTime = registTime;
    }

    public Date getLastConnectTime() {
        return lastConnectTime;
    }

    public void setLastConnectTime(Date lastConnectTime) {
        this.lastConnectTime = lastConnectTime;
    }
}
