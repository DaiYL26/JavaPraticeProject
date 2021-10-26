package com.example.login.exception;

import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public String handlerNotLogin(NotLoginException e){

        System.out.println("用户未登录！抛出异常：" + e.getClass().getName());
        System.out.println(e.getType());
        System.out.println(e.getLoginType());
        return "tologin";
    }

}
