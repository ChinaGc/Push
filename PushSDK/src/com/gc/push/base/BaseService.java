package com.gc.push.base;

import android.app.Service;

import com.gc.push.PushSDKApplication;

public abstract class BaseService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        ((PushSDKApplication) getApplication()).addService(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ((PushSDKApplication) getApplication()).removeService(this);
    }
}
