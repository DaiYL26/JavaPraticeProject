package com.example.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.dao.UserMapper;
import com.example.login.model.User;
import com.example.login.service.MailService;
import com.example.login.service.UserService;
import com.example.login.utils.PasswordUtils;
import com.example.login.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private final MailService mailService;

    private final UserMapper userMapper;

    private final StringRedisTemplate redisTemplate;

    public UserServiceImpl(MailService mailService, UserMapper userMapper, StringRedisTemplate redisTemplate) {
        this.mailService = mailService;
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 检测账号密码是否正确
     * @return 是否登录成功
     */
    @Override
    public UserVo checkUser(String account, String pwd) {

        UserVo userVo = new UserVo();

        //获取用户信息
        User user = userMapper.checkUser(account);

//        redisTemplate.opsForValue().setIfAbsent(user.getId() + ":todayNum", "0");

        if (user == null || !PasswordUtils.match(pwd, user.getPwd())) {
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

            newPassword = PasswordUtils.getEncodePwd(newPassword);

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
    public boolean register(String registerMail, String registerPhone, String registerPassword, String nickName,String registerCode) {

        if (mailService.verify(registerMail, registerCode)) {

            registerPassword = PasswordUtils.getEncodePwd(registerPassword);

            int i = userMapper.registerAccount(registerMail, registerPhone, registerPassword, nickName);

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

        return user != null;
    }

    @Override
    public UserVo getUserInfo(Integer id) {
        User res = userMapper.selectOne(new QueryWrapper<User>().eq("id", id));
        if (res == null) {
            return null;
        }

        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(res, userVo);

        return userVo;
    }

    @Override
    public UserVo checkCodeLogin(String mail, String code) {

        if (mailService.verify(mail, code)) {

            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mail", mail);
            User user = userMapper.selectOne(queryWrapper);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            return userVo;
        }

        return null;
    }

}
