package com.example.login;

import com.example.login.dao.UserMapper;
import com.example.login.model.User;
import com.example.login.service.MailService;
import com.example.login.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.nio.charset.StandardCharsets;

@SpringBootTest
class LoginApplicationTests {

    @Autowired
    MailService mailService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    @Test
    void contextLoads() throws InterruptedException {

//        userService.register("1516162635@qq.com", "")
//        int x = mailService.sendVerifyCode("877669110@qq.com");
//        System.out.println(x);
//        Thread.sleep(3000);
//        boolean b = userService.resetPassword("877669110@qq.com", "12345678", String.valueOf(x));
//        System.out.println(b);
//        System.out.println(userService.checkUser("877669110@qq.com", "123456"));
        int i = userMapper.updateAccount("16287872@qq.com", "1234567");
        System.out.println(i);
    }

}
