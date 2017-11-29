package roc.gc.push.nio;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: Request
 * @Description:NIO请求(服务器构建)
 * @author guocan
 * @date 2017年8月29日 下午5:36:35
 *
 */
public class Request {

    private Map<String, Object> parameters = new HashMap<>();

    public Request() {

    }

    public Request(Map<String, Object> parameters) {
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            this.parameters.put(entry.getKey(), entry.getValue());
        }
        this.parameters = parameters;
    }

    public Object getParameter(String key) {
        return parameters.get(key);
    }

    public void put(String key, Object value) {
        this.parameters.put(key, value);
    }
}
