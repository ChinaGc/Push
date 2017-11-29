package roc.gc.push.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import roc.gc.push.core.enums.PushStatus;
import roc.gc.push.dao.MessageDao;
import roc.gc.push.pojo.Message;
import roc.gc.push.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;

    // 查询指定用户等待接收的消息
    @Override
    public List<Message> findToAcceptorMessage(String acceptorUserToken) {
        Message message = new Message();
        message.setAcceptorUserToken(acceptorUserToken);
        message.setStatus(PushStatus.TO_PUSH.getStatus());
        return messageDao.findMessageList(message);
    }

    // 查询指定用户等待发送的消息
    @Override
    public List<Message> findToSendMessage(String sendUserToken) {
        Message message = new Message();
        message.setSendUserToken(sendUserToken);
        message.setStatus(PushStatus.TO_PUSH.getStatus());
        return messageDao.findMessageList(message);
    }

    @Override
    public void addMessage(Message message) {
        this.messageDao.addMessage(message);
    }

    @Override
    public void updateMesageState(Message message) {
        messageDao.upDateMessage(message);
    }

}
