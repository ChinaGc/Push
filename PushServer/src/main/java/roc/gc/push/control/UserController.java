package roc.gc.push.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import roc.gc.push.interceptor.ExceptionInterceptor;
import roc.gc.push.pojo.User;
import roc.gc.push.service.UserService;
import roc.gc.push.vo.Result;

@Controller
@RequestMapping("/user")
public class UserController {
	final static Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @RequestMapping("/registUser")
    @ResponseBody
    public Result registUser() {
        User user = new User();
        userService.addUser(user);
        Result result = new Result();
        result.setData(user.getToken());
        result.setMsg("ok");
        result.setSuccess(true);
        log.info("用户注册：{}",JSON.toJSONString(result));
        return result;
    }
}
