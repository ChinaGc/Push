package roc.gc.push.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

/**
 * 
 * @ClassName: ExceptionInterceptor
 * @Description: 全局异常处理
 * @author guocan
 * @date 2017年9月20日 下午4:08:54
 *
 */
public class ExceptionInterceptor extends HandlerInterceptorAdapter {
	final static Logger log = LoggerFactory.getLogger(ExceptionInterceptor.class);

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        if (ex != null) {
            String msg = "";
            if (ex instanceof NullPointerException) {
                msg = "空指针异常";
            } else if (ex instanceof IOException) {
                msg = "文件读写异常";
            } else {
                msg = "数据有误，请仔细检查";
            }
            logger(request, handler, ex);
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("success", false);
            result.put("msg", msg);
            writerJson(response, result);
        } else {
            super.afterCompletion(request, response, handler, ex);
        }
    }

    public void writerJson(HttpServletResponse response, Object object) {
        try {
            response.setContentType("text/html;charset=utf-8");
            writer(response, JSON.toJSONString(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void writer(HttpServletResponse response, String str) {
        try {
            // 设置页面不缓存
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = null;
            out = response.getWriter();
            out.print(str);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录日志
     * 
     * @param request
     * @param handler
     * @param ex
     */
    public void logger(HttpServletRequest request, Object handler, Exception ex) {
        StringBuffer msg = new StringBuffer();
        msg.append("异常拦截日志");
        msg.append("[uri＝").append(request.getRequestURI()).append("]");
        Enumeration<String> enumer = request.getParameterNames();
        while (enumer.hasMoreElements()) {
            String name = (String) enumer.nextElement();
            String[] values = request.getParameterValues(name);
            msg.append("[").append(name).append("=");
            if (values != null) {
                int i = 0;
                for (String value : values) {
                    i++;
                    msg.append(value);
                    if (i < values.length) {
                        msg.append("｜");
                    }
                }
            }
            msg.append("]");
        }
        log.error(msg.toString(), ex);
    }

}
