package com.sensing.core.utils.task;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.sensing.core.utils.ResponseBean;
import net.sf.json.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.ws.rs.core.Response.ResponseBuilder;


public class GlobalExceptionHandler implements HandlerExceptionResolver {
    protected static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static String getStackTrace(Throwable ex) {
        ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        try {
            ex.printStackTrace(new java.io.PrintWriter(buf, true));
            return buf.toString();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                buf.close();
            } catch (Exception a) {
            }
        }
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) {
        try {
            ResponseBean result = new ResponseBean();
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("success", false);
            // 为安全起见，只有业务异常我们对前端可见，否则统一归为系统异常
            //如果该 异常类型是系统 自定义的异常，直接取出异常信息。
//            CustomException customException = null;
//            try {
//                if(ex instanceof CustomException){
//                    customException = (CustomException)ex;
//                    //错误信息
//                    response.getWriter().write("333333333");
//                }else
//                    response.getWriter().write("ddddddddd");
//            } catch (IOException e) {
//                logger.error("与客户端通讯异常:"+ e.getMessage(), e);
//                e.printStackTrace();
//            }
//

            JsonObject object=new JsonObject();
            object.addProperty("error",100);
            object.addProperty("message","faile");



            writer.write(object.toString());
            writer.flush();
            writer.close();
        } catch (IOException eexx) {
            eexx.printStackTrace();
        }

        return null;
    }

    public static void main(String args[]) {

        JsonObject object=new JsonObject();
        object.addProperty("error",100);
        object.addProperty("message","faile");

//        ResponseBean result = new ResponseBean();
//        result.setError(100);
        System.out.println(object.toString());



    }
}