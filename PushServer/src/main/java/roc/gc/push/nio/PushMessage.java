package roc.gc.push.nio;

import java.util.HashMap;
import java.util.Map;

import roc.gc.push.core.enums.MessageType;

/**
 * 推送消息bean
 * 
 * @author gc
 *
 */
public class PushMessage {
    private Map<String, Object> map;// 推送消息的全部属性

    private MessageBody body;// 消息本身

    private String receiver;// 接收者

    private String sender;// 发送者

    private String type;// 推送消息类型

    private String sequence;// 唯一标志

    private PushMessage() {
        map = new HashMap<String, Object>();
        sequence = SequenceCreater.createSequence();
    }

    public static PushMessage createPushMessage(MessageType type, String receiver, String sender, MessageBody body) {
        PushMessage msg = new PushMessage();
        msg.type = getTypeString(type);
        msg.receiver = receiver;
        msg.sender = sender;
        msg.body = body;
        return msg;
    }

    public Map<String, Object> getMap() {
        map.put("receiver", receiver);
        map.put("type", type);
        map.put("sender", sender);
        map.put("sequence", sequence);
        map.put("from", "request");// 推送来源
        map.putAll(body.getMap());
        return map;
    }

    public void setBody(MessageBody body) {
        this.body = body;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public MessageBody getBody() {
        return body;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    private static String getTypeString(MessageType type) {
        switch (type) {
        case TEXT:
            return "text";
        /*
         * case IMAGE: return "image"; case INVITATION: return "invitation";
         * case REINVITATION: return "reinvitation";
         */
        default:
            break;
        }
        return null;
    }

    // private Object getMessageBody(MessageBody body) {
    // if (body instanceof TextBody) {
    // return ((TextBody) body).getContent();
    // }
    // /*
    // * else if (body instanceof InvitationBody) { return ((InvitationBody)
    // * body).getContent(); } else if (body instanceof ReInvitationBody) {
    // * return ((ReInvitationBody) body).getContent(); }
    // */
    // return null;
    // }

}
