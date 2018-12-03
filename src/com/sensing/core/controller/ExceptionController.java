//package com.sensing.core.controller;
//
//import com.sensing.core.utils.ResponseBean;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.MissingServletRequestParameterException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
////
////@ControllerAdvice
////@SuppressWarnings("unchecked")
////public class ExceptionController {
////    @ExceptionHandler(Exception.class)
////    @ResponseBody
////    public ResponseBean exception(Exception ex){
////        if(ex instanceof HttpRequestMethodNotSupportedException)
////            return new ResponseBean(1,"您的访问方式有误",null);
//////        if(ex instanceof DataNotCompletedException)
//////            return new JsonData(1,"数据残缺异常",null);
////        if(ex instanceof MissingServletRequestParameterException)
////            return new ResponseBean(1,"请查看您的参数列表",null);
////        return new ResponseBean(1,"未知异常",null);
////    }
////
////}
