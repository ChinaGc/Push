package com.gc.lib;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.gc.lib.callback.RequestCallBack;
import com.gc.lib.core.PacketConnector;
import com.gc.lib.core.PacketConnector.ConnectListener;
import com.gc.lib.core.PacketConnector.IOListener;
import com.gc.lib.core.Request;
import com.gc.lib.enums.PushEnum;

/**
 * 
 * @ClassName: PushManager
 * @Description: 推送管理者 管理所有的推送 连接行为
 * @author guocan
 * @date 2017-9-14 下午2:04:24
 * 
 */
public class PushManager {
    private static PushManager instance;

    private Context context;

    private Map<String, String> headers = new HashMap<String, String>();

    private String authSequence;

    // 连接器
    private PacketConnector connector;

    private List<PushConnectListener> connectListeners = new LinkedList<PushConnectListener>();

    private OnPushListener pushListener;

    private Map<String, Request> requests = new LinkedHashMap<String, Request>();

    private Thread mainThread;

    private Handler handler = new Handler();

    public static PushManager getInstance() {
        if (instance == null) {
            synchronized (PushManager.class) {
                if (instance == null) {
                    instance = new PushManager();
                }
            }
        }
        return instance;
    }

    private PushManager() {
        // context = HMChat.getContext();
        mainThread = Thread.currentThread();
    }

    /**
     * 初始化连接用户的安全信息
     * 
     * @param account
     * @param token
     */
    // public void initAccount(String account, String token) {
    // headers.put("account", account);
    // headers.put("token", token);
    // }

    /**
     * 
     * @Title: auth
     * @Description: 连接并认证 请求推送服务器行为
     * @author guocan
     * @param token
     *            推送服务器中注册时返回的唯一设备标识
     */
    public void auth(final String token) {
        headers.put("token", token);
        new Thread() {
            public void run() {
                // 创建请求
                Request request = new Request();
                request.setParameter("token", token);
                if (connector == null) {
                    connector = new PacketConnector(PUSHURL.PUSH_SERVER_HOST, PUSHURL.PUSH_SERVER_PORT, 3);
                }
                conncectPushServer();
                // authSequence = request.getRequestSequence();
                connector.addRequest(request);
            };
        }.start();
    }

    public void closeSocket() {
        if (connector != null && connector.isConnected()) {
            connector.disconnect();
            connector = null;
        }
    }

    private void conncectPushServer() {
        if (connector != null) {
            connector.connect();
            // 设置输入输出监听
            connector.setIOListener(ioListener);
            // 设置连接监听
            connector.setConnectListener(connectListener);
        }
    }

    /**
     * 添加连接监听
     * 
     * @param listener
     */
    public void addConnectionListener(PushConnectListener listener) {
        if (!connectListeners.contains(listener)) {
            connectListeners.add(listener);
        }
    }

    /**
     * 移除连接监听
     * 
     * @param listener
     */
    public void removeConnectionListener(PushConnectListener listener) {
        if (connectListeners.contains(listener)) {
            connectListeners.remove(listener);
        }
    }

    /**
     * 添加消息推送监听
     * 
     * @param listener
     */
    public void setPushListener(OnPushListener listener) {
        this.pushListener = listener;
    }

    /**
     * 发送消息
     * 
     * @param message
     * @param callBack
     */
    // public void sendMessage(final ChatMessage message, final HMChatCallBack
    // callBack) {
    // new Thread() {
    // public void run() {
    // if (connector == null) {
    // connector = new PacketConnector(HMURL.BASE_HM_HOST, HMURL.BASE_HM_PORT,
    // 3);
    // }
    //
    // conncectPushServer();
    //
    // addRequest(message, callBack);
    // }
    // }.start();
    // }

    // private void addRequest(final ChatMessage message, final HMChatCallBack
    // callBack) {
    // // 加入到请求map中 为以后的response做处理
    // ChatRequest request = new ChatRequest(callBack, message);
    // requests.put(request.getSequence(), request);
    //
    // connector.addRequest(request);
    // }

    // 监听IO行为
    private IOListener ioListener = new IOListener() {

        @Override
        public void onOutputFailed(Request request, Exception e) {
            // 消息发送失败，通知回调，让显示层做处理
            RequestCallBack callBack = request.getRequestCallBack();
            if (callBack != null) {
                callBack.onError(PushEnum.ERROR.getStatus(), PushEnum.ERROR.getDesc());
            }
        }

        // 只对服务端返回消息进行业务处理
        @SuppressWarnings("unchecked")
        @Override
        public void onInputComed(IoSession session, Object message) {
            if (message instanceof String) {
                String json = ((String) message).trim();
                Map<String, Object> result = JSON.parseObject(json);
                String from = (String) result.get("from");
                String sequence = (String) result.get("sequence");
                if (from.equalsIgnoreCase("request")) {// 服务器推送消息
                    // 这里只存在文本消息的情况
                    if (pushListener.onPush("TextMsgAction", result)) {
                        session.write("{from:'response',sequence:'" + sequence + "',success:" + true + "}");
                    } else {
                        session.write("{from:'response',sequence:'" + sequence + "',success:" + false + ",errorCode:" + PushEnum.CLIENTFAILED.getStatus() + ",errorMsg:'" + PushEnum.CLIENTFAILED.getDesc() + "'}");
                    }
                } else if (from.equalsIgnoreCase("response")) {// 客户端请求消息返回
                    final boolean success = (boolean) result.get("success");
                    // 这里只存在客户端认证 服务端向客户端响应的情况
                    // 认证callback
                    ListIterator<PushConnectListener> iterator = connectListeners.listIterator();
                    while (iterator.hasNext()) {
                        final PushConnectListener listener = iterator.next();
                        // 在主线程中调用
                        if (Thread.currentThread() != mainThread) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (success) {
                                        listener.onAuthSuccess();
                                    } else {
                                        listener.onAuthFailed();
                                    }
                                }
                            });
                        } else {
                            if (success) {
                                listener.onAuthSuccess();
                            } else {
                                listener.onAuthFailed();
                            }
                        }
                    }
                }

            }
        }
    };

    private ConnectListener connectListener = new ConnectListener() {

        @Override
        public void onReConnecting() {
            ListIterator<PushConnectListener> iterator = connectListeners.listIterator();
            while (iterator.hasNext()) {
                PushConnectListener listener = iterator.next();
                listener.onReconnecting();
            }
        }

        @Override
        public void onDisconnected() {
            Log.d("HM", "onDisconnected");
            ListIterator<PushConnectListener> iterator = connectListeners.listIterator();
            while (iterator.hasNext()) {
                PushConnectListener listener = iterator.next();
                listener.onDisconnected();
            }
            authSequence = null;
            connector = null;
        }

        @Override
        public void onConnecting() {
            ListIterator<PushConnectListener> iterator = connectListeners.listIterator();
            while (iterator.hasNext()) {
                PushConnectListener listener = iterator.next();
                listener.onConnecting();
            }
        }

        // 已经建立连接
        @Override
        public void onConnected() {
            ListIterator<PushConnectListener> iterator = connectListeners.listIterator();
            while (iterator.hasNext()) {
                PushConnectListener listener = iterator.next();
                listener.onConnected();
            }
        }
    };

    public interface PushConnectListener {
        /**
         * 正在连接
         */
        void onConnecting();

        /**
         * 已经连接
         */
        void onConnected();

        /**
         * 已经断开连接
         */
        void onDisconnected();

        /**
         * 正在重试连接
         */
        void onReconnecting();

        /**
         * 用户认证失败
         */
        void onAuthFailed();

        /**
         * 用户认证成功
         */
        void onAuthSuccess();
    }

    public interface OnPushListener {
        boolean onPush(String type, Map<String, Object> data);
    }
}
