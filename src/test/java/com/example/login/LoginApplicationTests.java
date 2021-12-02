package com.example.login;

import com.example.login.config.RequestDataHelper;
import com.example.login.dao.*;
import com.example.login.service.*;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

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


    @Autowired
    private SearchService searchService;

    @Autowired
    HomePageService homePageService;

    @Autowired
    LearnService learnService;

    @Autowired
    WordMapper wordMapper;

    @Autowired
    DictMapper dictMapper;

    @Autowired
    RecordMapper recordMapper;

    @Autowired
    ReviewPriorMapper reviewPriorMapper;



    @Test
    void contextLoads() throws InterruptedException, TException {

        System.out.println(redisTemplate.opsForValue().get("name"));

    }
}
