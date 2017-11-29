package roc.gc.push.dao;

import java.util.List;

import roc.gc.push.pojo.Message;

public interface MessageDao {
    public Message findMessageById(Long msgId);

    public Message findMessage(Message msg);

    public List<Message> findMessageList(Message msg);

    public int findMessageCount(Message msg);

    public void addMessage(Message msg);

    public void upDateMessage(Message msg);
}
