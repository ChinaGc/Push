package roc.gc.push.control;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import roc.gc.push.control.action.PushAction;
import roc.gc.push.nio.Request;
import roc.gc.push.service.MessageService;
import roc.gc.push.service.UserService;
import roc.gc.push.vo.PushRequestModel;
import roc.gc.push.vo.Result;

/**
 * 
 * @ClassName: PushController
 * @Description: 接收推送请求的控制器
 * @author guocan
 * @date 2017年9月2日 下午3:51:10
 *
 */
@Controller
@RequestMapping("/push")
public class PushController {

    @Resource(name = "action:push") // 得到推送行为
    private PushAction pushAction;

    @Resource
    private MessageService messageService;

    @Resource
    private UserService userService;

    /**
     * 
     * @Title: requestPush
     * @Description: http推送消息
     * @author guocan
     * @param receiver
     *            推送消息接收者
     * @param body
     *            推送内容
     * @return
     */
    @RequestMapping("/request")
    @ResponseBody
    public Result requestPush(PushRequestModel model) {
        Result result = new Result();
        Map<String, Object> map = new HashMap<>();
        map.put("receiver", model.getReceiver());
        map.put("body", model.getBody());
        map.put("sender", model.getSender());
        Request request = new Request(map);
        pushAction.doAction(request, null);
        result.setMsg("ok");
        result.setSuccess(true);
        return result;
    }
}
