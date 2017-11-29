package com.gc.lib.core;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.gc.lib.callback.RequestCallBack;
import com.gc.lib.utils.SequenceCreater;

/**
 * 
 * @ClassName: Request
 * @Description: 请求
 * @author guocan
 * @date 2017-9-14 下午2:06:32
 * 
 */
public class Request {

    private Map<String, Object> parameters;

    private RequestCallBack requestCallBack;

    // 一次请求的标识
    private String sequence;

    public Request() {
        parameters = new HashMap<String, Object>();
        sequence = SequenceCreater.createSequence();
    }

    public Request(Map<String, Object> parameters) {
        this.parameters = parameters;
        sequence = SequenceCreater.createSequence();
    }

    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }

    public Object getParameter(String key) {
        return this.parameters.get(key);
    }

    public String getRequestSequence() {
        return this.sequence;
    }

    // 获取发送的json格式数据
    public String getTransport() {
        parameters.put("action", "auth");
        parameters.put("from", "request");
        parameters.put("sequence", sequence);
        parameters.put("token", parameters.get("token"));
        return JSON.toJSONString(parameters);
    }

    public RequestCallBack getRequestCallBack() {
        return requestCallBack;
    }

    public void setRequestCallBack(RequestCallBack requestCallBack) {
        this.requestCallBack = requestCallBack;
    }
}
