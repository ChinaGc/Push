package com.gc.push;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gc.lib.utils.CommonUtil;
import com.gc.push.service.PushCoreService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // 连接服务器
    public void connect(View view) {
        if (!CommonUtil.isServiceRunning(this, PushCoreService.class)) {
            this.startService(new Intent(this, PushCoreService.class));
        }
    }

    // 断开连接
    public void disconnect(View view) {
        this.stopService(new Intent(this, PushCoreService.class));
    }
}
