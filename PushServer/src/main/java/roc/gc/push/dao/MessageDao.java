package roc.gc.push.dao;

import java.util.List;

import roc.gc.push.pojo.Message;
/**
 * 
* @ClassName: MessageDao
* @Description: MessageDao
* @author guocan
* @date 2017年12月27日 下午7:31:10
*
 */
public interface MessageDao {
    public Message findMessageById(Long msgId);

    public Message findMessage(Message msg);

    public List<Message> findMessageList(Message msg);

    public int findMessageCount(Message msg);

    public void addMessage(Message msg);

    public void upDateMessage(Message msg);
}
