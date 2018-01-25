package roc.gc.push.nio;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSON;

import roc.gc.push.core.Action;
import roc.gc.push.core.ContextUtil;
import roc.gc.push.core.enums.ErrorCode;

/**
 * 
 * @ClassName: SocketDispatcher IoHandler
 * @Description: 服务端 消息接收与业务处理的IoHandler 逻辑：A--request msg--> server --push
 *               msg-->B
 * @author guocan
 * @date 2017年8月29日 下午3:05:32
 *
 */
public class SocketDispatcher implements IoHandler {
    private Logger logger = LoggerFactory.getLogger(SocketDispatcher.class);

    // 存放 nio 行为的集合 例如 进行连接时认证行为 添加好友行为 文本发送行为
    private static Map<String, Action> actionPool = new LinkedHashMap<String, Action>();

    // 存放客户端的会话集合(sessionId,session)
    private static Map<Long, IoSession> sessionPool = new LinkedHashMap<Long, IoSession>();

    // 记录登录的用户(userToken,sessionId)
    private static Map<String, Long> accountPool = new LinkedHashMap<String, Long>();

    // 记录推送的请求
    private static Map<String, PushRequest> pushPool = new LinkedHashMap<String, PushRequest>();

    // 用于真正执行推送任务的推送请求队列
    private static LinkedBlockingQueue<PushRequest> pushQueue = new LinkedBlockingQueue<PushRequest>(256);

    // 推送任务的工作线程数
    private static int pushWorkerSize = 16;

    // 线程池
    private static ExecutorService requestWorkerThreadPool;

    private String classPath;

    public SocketDispatcher() {
        ApplicationContext context = ContextUtil.getApplicationContext();
        classPath = context.getClassLoader().getResource("/").getPath();
        logger.info("path : " + classPath);
        // 获取spring容易管理的bean
        String[] names = context.getBeanDefinitionNames();
        for (String string : names) {
            logger.info("name : " + string);
        }
        // 新建线程池
        requestWorkerThreadPool = Executors.newFixedThreadPool(128);
        // 从容器中获取所有的Action
        scanNioAction();
        // 初始化推送的线程工作者 时刻准备做推送工作
        for (int i = 0; i < pushWorkerSize; i++) {
            new Thread(new PushWorker()).start();
        }
    }

    /**
     * 从Spring容器中获取NioAction 并添加进actionPool
     */
    private void scanNioAction() {
        ApplicationContext context = ContextUtil.getApplicationContext();
        String[] names = context.getBeanDefinitionNames();
        for (String name : names) {
        	logger.info("name : " + name);
            if (name.startsWith("action:")) {
                String replace = name.replace("action:", "");
                actionPool.put(replace, (Action) context.getBean(name));
            }
        }
    }

    /**
     * 
     * @Title: addPush
     * @Description: 向推送队列中添加一个推送请求（激活推送工作者线程） 并且向pushPool中添加一个推送
     * @author guocan
     * @param request
     */
    public void addPush(PushRequest request) {
        String sequence = request.getSequence();
        try {
            pushPool.put(sequence, request);
            pushQueue.put(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
            pushPool.remove(sequence);
            PushCallback callBack = request.getCallBack();
            if (callBack != null) {
                callBack.onError(1, "服务器问题");
            }
        }
    }

    /**
     * 
     * @Title: isOnline
     * @Description: 判断当前用户是否在线
     * @author guocan
     * @param userToken
     * @return
     */
    public boolean isOnline(String userToken) {
        return accountPool.containsKey(userToken);
    }

    /**
     * 
     * @ClassName: PushWorker
     * @Description: 真正执行推送的工作，没有推送任务则全部等待
     * @author guocan
     * @date 2017年8月29日 下午2:55:13
     *
     */
    private class PushWorker implements Runnable {
        @SuppressWarnings("null")
		@Override
        public void run() {
            while (true) {
                PushRequest request = null;
                try {
                    // take阻塞 等待pushQueue中添加推送请求，获取推送请求并从队列中移除
                    request = pushQueue.take();
                    String receiver = request.getReceiver();
                    IoSession session = sessionPool.get(accountPool.get(receiver));
                    if (session != null) {
                        System.out.println("PushWorker-----------------:" + request.getSequence());
                        session.write(request.getTransport());
                    } else {
                        PushCallback callBack = request.getCallBack();
                        if (callBack != null) {
                            callBack.onError(1, "服务器异常");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    PushCallback callBack = request.getCallBack();
                    if (callBack != null) {
                        callBack.onError(1, "服务器异常");
                    }
                }
            }
        }
    }

    /**
     * 用户连接时sessionCreated callback
     */
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.info("sessionCreated");
    }

    /**
     * 用户连接时sessionOpened callback
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        logger.info("sessionOpened");
    }

    /**
     * 断开连接时 sessionClosed callback
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.info("sessionClosed");
        long id = session.getId();
        sessionPool.remove(id);
        accountPool.remove(session.getAttribute("account"));
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        logger.info("sessionIdle");
    }

    /**
     * 出现异常callback
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.info("exceptionCaught");
        logger.error("链接异常", cause);
        // if (cause instanceof FormatExcetion) {
        long id = session.getId();
        sessionPool.remove(id);
        accountPool.remove(session.getAttribute("account"));
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        response.put("errorCode", ErrorCode.SERVER_ERROR.getCode());
        response.put("errorMsg", ErrorCode.SERVER_ERROR.getDesc());
        session.write(JSON.toJSONString(response));
        session.closeOnFlush();
        // }
    }
    /**
     * 收到客户端消息的两种情况 1.客户端连接认证 2.服务端推送消息反馈
     */
    @Override
    public void messageReceived(final IoSession session, Object message) throws Exception {
        //logger.info("messageReceived : " + message);
        try {
            // 客户端请求示例：
            // {type:"request",sequence:"标识一次发送过程",action:"auth",token:"发送者用户token"}
            // 客户端响应示例：
            // {type:"response",sequence:"标识一次发送过程",status:"true客户端成功响应"}

            Map<String, Object> receiveMsg = JSON.parseObject((String)message);
            String sequence = (String) receiveMsg.get("sequence");
            String type = (String) receiveMsg.get("from");
            System.out.println("messageReceived----------------:" + sequence);
            if ("request".equalsIgnoreCase(type)) {
                // 客户端发送端请求
                String act = (String) receiveMsg.get("action");
                String token = (String) receiveMsg.get("token");// 用户标识
                final Action action = actionPool.get(act);
                // 认证请求
                if ("auth".equalsIgnoreCase(act)) {
                    // 判断是否有其他设备已经登录同一账号
                    Long sessionId = accountPool.get(token);
                    if (sessionId != null) {
                        // 将已经在线的用户挤下去 关闭已存在的IOSession
                        IoSession ioSession = sessionPool.get(sessionId);// 已经存在IOSession
                        Map<String, Object> response = new HashMap<String, Object>();
                        response.put("success", false);
                        response.put("type", "response");
                        response.put("sequence", sequence);
                        response.put("errorCode", ErrorCode.LOGIN_ON_ONTHER.getCode());
                        response.put("errorMsg", ErrorCode.LOGIN_ON_ONTHER.getDesc());
                        ioSession.write(JSON.toJSONString(response));
                        ioSession.closeOnFlush();
                    }
                    // 添加到管理中来
                    sessionId = session.getId();
                    sessionPool.put(sessionId, session);
                    accountPool.put(token, sessionId);
                    session.setAttribute("account", token);
                }
                // 包含认证的请求处理
                if (action != null) {
                    final Response response = new Response(session);
                    response.put("from", "response");// 响应客户端
                    response.put("sequence", sequence);
                    requestWorkerThreadPool.submit(new Runnable() {
                        @Override
                        public void run() {
                            // 相应的action 做相应的业务逻辑(本推送系统这里只会有认证Action)
                            action.doAction(new Request(receiveMsg), response);
                        }
                    });
                }
            } else if ("response".equalsIgnoreCase(type)) {
                // 客户端发送端响应 response
                boolean success = (boolean) receiveMsg.get("success");// 客户端响应是否成功
                // 消息发送结果只有 成功或者 失败,不需要返回对象
                if (success) {
                    PushRequest request = pushPool.remove(sequence);// 得到服务端响应的那个推送PushRequest
                    if (request != null) {
                        PushCallback callBack = request.getCallBack();
                        if (callBack != null) {
                            callBack.onSuccess();
                        }
                    }
                } else {
                    PushRequest request = pushPool.remove(sequence);
                    if (request != null) {
                        PushCallback callBack = request.getCallBack();
                        if (callBack != null) {
                            int errorCode = (int) receiveMsg.get("errorCode");
                            String errorString = (String) receiveMsg.get("errorMsg");
                            callBack.onError(errorCode, errorString);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // throw new FormatExcetion();
            e.printStackTrace();
        }
    }

    /**
     * 消息发送成功callback
     */
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        logger.info("messageSent");
    }
}
