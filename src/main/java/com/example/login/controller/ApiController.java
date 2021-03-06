package com.example.login.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.login.service.UserService;
import com.example.login.vo.Result;
import com.example.login.vo.UserVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    private final UserService userService;

    public ApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result login(String account, String password) {
        log.info(account + " try login at " + new Date().toString());
        UserVo userVo = userService.checkUser(account, password);

        if (userVo != null) {
            StpUtil.login(userVo.getId());
            return Result.success(userVo);
        } else {
            return Result.fail(500, "账号密码错误");
        }
    }

    @PostMapping("/verify")
    public Result getVerifyCode(String mail) {

        boolean isSetVerifyCode = userService.setVerifyCode(mail);

        if (isSetVerifyCode) {
            return Result.success(Boolean.TRUE);
        } else {
            return Result.fail(500, "验证码获取失败");
        }

    }

    @PostMapping("/reset")
    public Result reset(String mail, String newPassword, String verifyCode) {
        boolean isReset = userService.resetPassword(mail, newPassword, verifyCode);

        if (isReset) {
            return Result.success(Boolean.TRUE);
        } else {
//            return Result.fail(500, "验证失败");
            if (!userService.isAccountExist(mail)) {
                return Result.fail(500, "不存在该账号");
            } else {
                return Result.fail(400, "验证失败");
            }
        }
    }

    @PostMapping("/register")
    public Result register(String mail, String phone, String password, String nickName, String verifyCode) {
        boolean isRegister = userService.register(mail, phone, password, nickName, verifyCode);

        if (isRegister) {
            return Result.success(Boolean.TRUE);
        } else {
//            return Result.fail(500, "验证失败");
            if (!userService.isAccountExist(mail)) {
                return Result.fail(400, "验证失败");
            } else {
                return Result.fail(500, "该邮箱已存在");
            }
        }
    }

    @PostMapping("/codeLogin")
    public Result codeLogin(String mail, String code) {
        UserVo userVo = userService.checkCodeLogin(mail, code);

        if (userVo == null) {
            return Result.fail(500, "验证码错误");
        } else {
            StpUtil.login(userVo.getId());
            return Result.success(userVo);
        }

    }

    @SaCheckLogin
    @GetMapping("/isLogin")
    public Result isLogin() {
//        StpUtil.checkLogin();
        Boolean login = StpUtil.isLogin();
        return Result.success(login);
    }

    @SaCheckLogin
    @PostMapping("/getUserInfo")
    public Result getUserInfo(Integer id) {
        UserVo userInfo = userService.getUserInfo(id);

        if (userInfo == null) {
            return Result.fail(500, "系统错误");
        } else {
            return Result.success(userInfo);
        }
    }

    @SaCheckLogin
    @GetMapping("/logout")
    public Boolean loginOut() {
        StpUtil.logout();
        return true;
    }

}
