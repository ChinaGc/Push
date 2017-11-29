package com.gc.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 开机启动服务
        // if (!CommonUtil.isServiceRunning(context, PushCoreService.class)) {
        // context.startService(new Intent(context, PushCoreService.class));
        // }
    }
}