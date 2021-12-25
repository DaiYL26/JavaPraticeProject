package com.example.login.service.impl;

import com.example.login.service.MailService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSenderImpl mailSender;

    private final StringRedisTemplate redisTemplate;

    public MailServiceImpl(JavaMailSenderImpl mailSender, StringRedisTemplate redisTemplate) {
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public int sendVerifyCode(String toMail) {

        int code = ( 111331 * ThreadLocalRandom.current().nextInt(1, 99999) ) % 1000000;
        code = Math.abs(code);

        if (code < 100000) {
            code += 100000;
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setText("您的验证码是：" + code + " , 有效时间为 60 秒");
        mailMessage.setFrom("1516162635@qq.com");
        mailMessage.setTo(toMail);
        mailMessage.setSubject("验证码");

        mailSender.send(mailMessage);

        String key = "verify:" + toMail;
        Boolean isSetCode = redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(code), 60, TimeUnit.SECONDS);

        if (isSetCode == null) {
            return -1;
        }

        if (isSetCode) {
            return code;
        } else {
            return -1;
        }
    }

    @Override
    public boolean verify(String mail, String verifyCode) {

        String key = "verify:" + mail;

        String code = redisTemplate.opsForValue().get(key);

        if (code != null && code.equals(verifyCode)) {
            redisTemplate.delete(key);
        }

        return verifyCode.equals(code);
    }

    @Override
    public boolean isExist(String mail) {
        String key = "verify:" + mail;
        String code = redisTemplate.opsForValue().get(key);
        return code != null;
    }


}
