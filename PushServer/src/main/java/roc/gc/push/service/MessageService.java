package roc.gc.push.service;

import java.util.List;

import roc.gc.push.pojo.Message;

public interface MessageService {

    public void addMessage(Message message);

    public void updateMesageState(Message message);

    public List<Message> findToAcceptorMessage(String acceptorUserToken);

    public List<Message> findToSendMessage(String sendUserToken);
}
