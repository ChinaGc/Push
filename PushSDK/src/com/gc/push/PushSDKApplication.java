package com.gc.push;

import java.util.LinkedList;
import java.util.List;

import android.app.Application;
import android.app.Service;
import android.os.Process;
import android.util.Log;

public class PushSDKApplication extends Application {
    private List<Service> services = new LinkedList<Service>();

    private int pid;// 进程id

    @Override
    public void onCreate() {
        super.onCreate();
        pid = Process.myPid();
        Log.d("PushSDKApplication", "init");
        // 初始化
        // HMChat.getInstance().init(this);
    }

    public void addService(Service service) {
        services.add(service);
    }

    public void removeService(Service service) {
        services.remove(service);
    }
}
