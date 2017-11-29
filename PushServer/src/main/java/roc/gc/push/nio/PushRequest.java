package roc.gc.push.nio;

import com.alibaba.fastjson.JSON;

/**
 * 推送请求实体
 * 
 * @author gc
 *
 */
public class PushRequest {
    private PushCallback callback;

    private PushMessage message;

    private String sequence;// PushMessage唯一标识

    public PushRequest(PushCallback callback, PushMessage msg) {
        this.callback = callback;
        this.message = msg;
        sequence = (String) this.message.getMap().get("sequence");
    }

    public String getSequence() {
        return sequence;
    }

    public String getTransport() {
        return JSON.toJSONString(this.message.getMap());
    }

    public PushCallback getCallBack() {
        return callback;
    }

    public String getReceiver() {
        return message.getMap().get("receiver").toString();
    }

}
