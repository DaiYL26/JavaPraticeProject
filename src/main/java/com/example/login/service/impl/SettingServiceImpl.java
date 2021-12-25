package com.example.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.dao.*;
import com.example.login.model.Plan;
import com.example.login.model.UserInfo;
import com.example.login.service.SettingService;
import com.example.login.vo.Result;
import com.example.login.vo.SettingPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingServiceImpl implements SettingService {

    private final PlanMapper planMapper;

    private final UserMapper userMapper;

    private final UserInfoMapper userInfoMapper;

    private final StringRedisTemplate redisTemplate;

    public SettingServiceImpl(PlanMapper planMapper, UserMapper userMapper, UserInfoMapper userInfoMapper, StringRedisTemplate redisTemplate) {
        this.planMapper = planMapper;
        this.userMapper = userMapper;
        this.userInfoMapper = userInfoMapper;
        this.redisTemplate = redisTemplate;
    }


    @Transactional
    @Override
    public Result updateRecord(Long userid, String username, Integer studyword, Integer reviewCnt)
    {
        if(username!=null)
        {
            userMapper.updateNickname(userid,username);
        }
        int plan = planMapper.updateCount(userid, studyword);
        int info = userInfoMapper.updateReviewCnt(userid, reviewCnt);

        if (username != null) {
            redisTemplate.opsForValue().set(userid + ":name", username);
        }
//        System.out.println("终于成功了！！！");

        if (plan == 0 || info == 0) {
            return Result.fail(500, "请先选择学习计划");
        }
        return Result.success("修改成功");
    }

    @Override
    public Result getSettingStatus(Long userid) {

        UserInfo userInfo = userInfoMapper.selectById(userid);
        Plan plan = planMapper.selectOne(new QueryWrapper<Plan>().eq("userid", userid).eq("isCur", 1));

        if (userInfo == null || plan == null) {
            return Result.fail(500, "请先选择学习计划");
        }
        SettingPageVo settingPageVo = new SettingPageVo(plan.getCount(), userInfo.getReviewCnt());

        return Result.success(settingPageVo);
    }
}

