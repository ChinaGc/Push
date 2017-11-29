package com.gc.push.action;

import java.util.Map;

import android.content.Context;
import android.util.Log;

/**
 * 
 * @ClassName: TextMsgAction
 * @Description: 接收文本消息行为
 * @author guocan
 * @date 2017-9-14 下午1:57:53
 * 
 */
public class TextMsgAction extends Action {

    @Override
    public String getAction() {
        return "TextMsgAction";
    }

    @Override
    public void doAction(Context context, Map<String, Object> data) {
        Log.i("TextMsgAction", data.toString());
    }
}
