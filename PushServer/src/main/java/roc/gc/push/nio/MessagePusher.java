package roc.gc.push.nio;

import org.springframework.stereotype.Component;

import roc.gc.push.core.ContextUtil;

/**
 * 服务器向客户端发送消息者 其实就是push
 * 
 * @author gc
 *
 */
@Component
public class MessagePusher {

    // @Autowired
    // SocketDispatcher socketDispatcher;

    public void push(PushMessage msg, PushCallback callback) {
        SocketDispatcher socketDispatcher = ContextUtil.getApplicationContext().getBean(SocketDispatcher.class);
        socketDispatcher.addPush(new PushRequest(callback, msg));
    }

    public boolean isOnline(String userToken) {
        SocketDispatcher socketDispatcher = ContextUtil.getApplicationContext().getBean(SocketDispatcher.class);
        return socketDispatcher.isOnline(userToken);
    }
}