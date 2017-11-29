package roc.gc.push.control.action;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import roc.gc.push.core.Action;
import roc.gc.push.core.enums.MessageType;
import roc.gc.push.core.enums.PushStatus;
import roc.gc.push.nio.MessagePusher;
import roc.gc.push.nio.PushCallback;
import roc.gc.push.nio.PushMessage;
import roc.gc.push.nio.Request;
import roc.gc.push.nio.Response;
import roc.gc.push.nio.body.TextBody;
import roc.gc.push.pojo.Message;
import roc.gc.push.service.MessageService;

/**
 * 
 * @ClassName: PushAction
 * @Description: 推送行为
 * @author guocan
 * @date 2017年8月29日 下午2:47:47
 *
 */
@Component(value = "action:push")
public class PushAction implements Action {

    @Autowired
    private MessagePusher messagePusher;

    @Autowired
    private MessageService messageService;

    @Override
    public void doAction(Request request, Response response) {
        if (response != null) {// 响应客户端
            response.put("success", true);
            response.put("from", "response");
            response.writeResponse();
        }
        String receiver = (String) request.getParameter("receiver");// 接收推送用户
        String body = (String) request.getParameter("body");
        String sender = (String) request.getParameter("sender");
        if (receiver != null && receiver.length() > 0) {
            String[] tokens = receiver.split(",");// 多个用户逗号分隔
            for (String token : tokens) {
                Message message = new Message();
                message.setAcceptorUserToken(token);
                message.setBody(body);
                message.setSendUserToken(sender);
                if (messagePusher.isOnline(token)) {// 接收者是否在线
                    message.setPushTime(new Date());
                    message.setStatus(PushStatus.PUSHING.getStatus());
                    messageService.addMessage(message);
                    // 推送
                    PushMessage pushMessage = PushMessage.createPushMessage(MessageType.TEXT, receiver, sender,
                            new TextBody(body));
                    messagePusher.push(pushMessage, new PushCallback() {
                        @Override
                        public void onSuccess() {
                            message.setStatus(PushStatus.PUSH_SUCCESS.getStatus());
                            messageService.updateMesageState(message);
                        }

                        @Override
                        public void onProgress(int progress) {

                        }

                        @Override
                        public void onError(int error, String msg) {
                            message.setStatus(PushStatus.PUSH_FAILED.getStatus());
                            messageService.updateMesageState(message);
                        }
                    });

                } else {
                    message.setStatus(PushStatus.TO_PUSH.getStatus());
                    messageService.addMessage(message);
                }
            }
        }
    }
}
