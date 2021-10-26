package com.example.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.dao.UserMapper;
import com.example.login.model.User;
import com.example.login.service.MailService;
import com.example.login.service.UserService;
import com.example.login.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {

    private final MailService mailService;

    private final UserMapper userMapper;

    public UserServiceImpl(MailService mailService, UserMapper userMapper) {
        this.mailService = mailService;
        this.userMapper = userMapper;
    }

    /**
     * 检测账号密码是否正确
     * @return 是否登录成功
     */
    @Override
    public UserVo checkUser(String account, String pwd) {

        UserVo userVo = new UserVo();
        User user = userMapper.checkUser(account, pwd);

        if (user == null) {
            return null;
        }

        BeanUtils.copyProperties(user, userVo);

        return userVo;
    }

    /**
     * 重置密码校验验证码，及设置新密码
     * @return 是否重置成功
     */
    @Override
    public boolean resetPassword(String mail, String newPassword, String verifyCode) {

        if (mailService.verify(mail, verifyCode)) {
            int i = userMapper.updateAccount(mail, newPassword);
            return i > 0;
        }

        return false;
    }

    /**
     * 注册账号，及校验验证码
     * @return 是否注册成功
     */
    @Override
    public boolean register(String registerMail, String registerPhone, String registerPassword, String registerCode) {

        if (mailService.verify(registerMail, registerCode)) {
            int i = userMapper.registerAccount(registerMail, registerPhone, registerPassword);
            return i > 0;
        }

        return false;
    }

    /**
     * 获取验证码
     * @return 验证码是否发生成功
     */
    @Override
    public boolean setVerifyCode(String mail) {

        if (mailService.isExist(mail)) {
            return false;
        }

        return mailService.sendVerifyCode(mail) != -1;
    }


    public boolean isAccountExist(String mail) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mail", mail);
        User user = userMapper.selectOne(queryWrapper);

        return user == null;
    }

}
