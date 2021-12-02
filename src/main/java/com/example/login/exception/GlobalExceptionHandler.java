package com.example.login.exception;

import cn.dev33.satoken.exception.NotLoginException;
import org.apache.thrift.TException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public String handlerNotLogin(NotLoginException e){

        System.out.println("用户未登录！抛出异常：" + e.getClass().getName());
        System.out.println(e.getType());
        System.out.println(e.getLoginType());
        return "tologin";
    }

    @ExceptionHandler(TException.class)
    @ResponseBody
    public String handlerTException(TException e) {
        return "查词服务挂了....";
    }

}
