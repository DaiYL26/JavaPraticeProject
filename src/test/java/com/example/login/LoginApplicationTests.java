package com.example.login;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.config.RequestDataHelper;
import com.example.login.dao.*;
import com.example.login.game.GameKiller;
import com.example.login.game.GameServerInitializer;
import com.example.login.game.GameSocketHandler;
import com.example.login.game.MatchPool;
import com.example.login.game.model.GameUser;
import com.example.login.model.ReviewPrior;
import com.example.login.service.*;
import com.example.login.service.impl.ReviewServiceImpl;
import com.example.login.utils.JSONUtils;
import com.example.login.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

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

    @Autowired
    ApplicationContext context;

    JedisPool jedisPool = new JedisPool("120.77.222.189", 63799);

    @Test
    void contextLoads() throws Exception {

        System.out.println(redisTemplate.hasKey("1640165168942"));

    }
}
