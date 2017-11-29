package roc.gc.push.core;

import roc.gc.push.nio.Request;
import roc.gc.push.nio.Response;

/**
 * 
 * @ClassName: Action
 * @Description: nio行为接口
 * @author guocan
 * @date 2017年8月29日 下午2:57:02
 *
 */
public interface Action {

    void doAction(Request request, Response response);
}
