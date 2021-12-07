package com.example.login;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.config.RequestDataHelper;
import com.example.login.dao.*;
import com.example.login.model.ReviewPrior;
import com.example.login.service.*;
import com.example.login.service.impl.ReviewServiceImpl;
import com.example.login.vo.Result;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Autowired
    ReviewServiceImpl reviewService;

    @Test
    void contextLoads() throws InterruptedException, TException, ParseException {

//        reviewService.updatePriorWord(16L, 50, 1);

    }
}
