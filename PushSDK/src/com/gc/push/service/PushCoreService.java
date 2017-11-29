package com.gc.push.service;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.gc.lib.PushManager;
import com.gc.lib.PushManager.OnPushListener;
import com.gc.lib.PushManager.PushConnectListener;
import com.gc.lib.utils.CommonUtil;
import com.gc.push.R;
import com.gc.push.action.Action;
import com.gc.push.base.BaseService;

/**
 * 
 * @ClassName: PushCoreService
 * @Description: 开机启动
 * @author guocan
 * @date 2017-9-11 下午2:11:36
 * 
 */
public class PushCoreService extends BaseService implements PushConnectListener, OnPushListener {
    private PushManager pushManager;

    private int reconnectCount = 0;// 重连次数

    private Map<String, Action> actionMaps = new HashMap<String, Action>();

    // 网络状态发生改变的广播接收者
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (CommonUtil.isNetConnected(PushCoreService.this)) {
                    // 网络已经连接
                    connectServer();
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("PushCoreService", "onCreate");
        pushManager = PushManager.getInstance();
        pushManager.addConnectionListener(this);// 本服务 希望得到实际的连接状态的变化通知
        pushManager.setPushListener(this);// 本服务希望得到 服务端送消息的接收通知
        // 注册监听
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // registerReceiver(mReceiver, mFilter);
        scanClass();
        // 连接服务器
        connectServer();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("PushCoreService", "onDestroy");
        pushManager.removeConnectionListener(this);
        unregisterReceiver(mReceiver);
        // 断开连接
        pushManager.closeSocket();
    }

    private void connectServer() {
        // TODO 连接推送服务器
        String token = "fa10923e-a834-4230-9873-ea85aac78e7e";
        pushManager.auth(token);
    }

    @Override
    public void onConnecting() {
        Log.d("PushCoreService", "onConnecting");
    }

    @Override
    public void onConnected() {
        reconnectCount = 0;
        Log.d("PushCoreService", "onConnected");
    }

    @Override
    public void onDisconnected() {
        Log.d("PushCoreService", "onDisconnected");
        if (CommonUtil.isNetConnected(PushCoreService.this)) {
            // 有网络的
            Log.d("Core", "网络断开重连");
            reconnectCount++;
            if (reconnectCount < 10) {
                connectServer();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onReconnecting() {

    }

    @Override
    public void onAuthFailed() {
        Toast.makeText(this, "onAuthFailed", Toast.LENGTH_SHORT).show();
        Log.d("PushCoreService", "onAuthFailed");
    }

    @Override
    public void onAuthSuccess() {
        Toast.makeText(this, "onAuthSuccess", Toast.LENGTH_SHORT).show();
        Log.d("PushCoreService", "onAuthSuccess");
    }

    // 扫描本应用中所有具有行为class
    private void scanClass() {
        String[] array = getResources().getStringArray(R.array.actions);
        if (array == null) {
            return;
        }
        String packageName = getPackageName();
        ClassLoader classLoader = getClassLoader();
        for (int i = 0; i < array.length; i++) {
            try {
                Class<?> clazz = classLoader.loadClass(packageName + "." + array[i]);
                Class<?> superclass = clazz.getSuperclass();
                if (superclass != null && Action.class.getName().equals(superclass.getName())) {
                    Action action = (Action) clazz.newInstance();
                    actionMaps.put(action.getAction(), action);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 收到消息推送，具体处理交给响应的
    @Override
    public boolean onPush(String action, Map<String, Object> data) {
        Log.d("PushCoreService", "onPush=action : " + action + " data : " + data);
        Action actioner = actionMaps.get(action);
        if (actioner != null) {
            actioner.doAction(this, data);
            return true;
        }
        return false;
    }
}
