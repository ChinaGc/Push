package roc.gc.push.nio;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSON;

public class Response {

    private IoSession session;

    private Map<String, Object> map = new HashMap<String, Object>();

    public Response(IoSession session) {
        this.session = session;
    }

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public void writeResponse() {
        String json = JSON.toJSONString(map);
        System.out.println(json);
        session.write(json);
    }

    public void disconnect() {
        session.closeOnFlush();
    }
}
