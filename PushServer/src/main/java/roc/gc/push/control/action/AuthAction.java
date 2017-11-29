package roc.gc.push.control.action;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import roc.gc.push.core.Action;
import roc.gc.push.core.enums.ErrorCode;
import roc.gc.push.core.enums.MessageType;
import roc.gc.push.core.enums.PushStatus;
import roc.gc.push.nio.MessagePusher;
import roc.gc.push.nio.PushCallback;
import roc.gc.push.nio.PushMessage;
import roc.gc.push.nio.Request;
import roc.gc.push.nio.Response;
import roc.gc.push.nio.body.TextBody;
import roc.gc.push.pojo.Message;
import roc.gc.push.pojo.User;
import roc.gc.push.service.MessageService;
import roc.gc.push.service.UserService;

/**
 * 
 * @ClassName: AuthAction
 * @Description: 用户认证行为(连接服务器)
 * @author guocan
 * @date 2017年8月29日 下午2::24
 *
 */
@Component(value = "action:auth")
public class AuthAction implements Action {

    @Resource
    private UserService userService;

    @Resource
    private MessageService messageService;

    @Resource
    private MessagePusher messagePusher;

    @Override
    public void doAction(Request request, Response response) {
        // 认证
        // 登录令牌
        String token = (String) request.getParameter("token");
        User user = userService.findUser(token);

        if (user == null) {
            response.put("success", false);
            response.put("errorCode", ErrorCode.USER_NOT_EXIST.getCode());
            response.put("errorMsg", ErrorCode.USER_NOT_EXIST.getDesc());
            response.writeResponse();
            response.disconnect();// 关闭会话IOSession
            return;
        }
        response.put("success", true);
        response.writeResponse();// 认证成功
        // 更新用户最后连接时间
        User pojo = new User();
        pojo.setId(user.getId());
        pojo.setLastConnectTime(new Date());
        userService.upDateUser(pojo);
        // 查询该用户等待推送的消息
        List<Message> toPushMessage = this.messageService.findToAcceptorMessage(token);
        for (Message message : toPushMessage) {
            // 更新消息为发送中
            message.setStatus(PushStatus.PUSHING.getStatus());
            message.setPushTime(new Date());
            messageService.updateMesageState(message);
            PushMessage pushMessage = PushMessage.createPushMessage(MessageType.TEXT, token, message.getSendUserToken(),
                    new TextBody(message.getBody()));
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
        }
    }
}
