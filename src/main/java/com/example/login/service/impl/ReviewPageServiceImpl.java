package com.example.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.dao.PlanMapper;
import com.example.login.dao.UserInfoMapper;
import com.example.login.model.Plan;
import com.example.login.model.UserInfo;
import com.example.login.service.ReviewPageService;
import com.example.login.vo.Result;
import com.example.login.vo.ReviewPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewPageServiceImpl implements ReviewPageService {

    private final StringRedisTemplate redisTemplate;

    private final UserInfoMapper userInfoMapper;

    private final PlanMapper planMapper;

    public ReviewPageServiceImpl(StringRedisTemplate redisTemplate, UserInfoMapper userInfoMapper, PlanMapper planMapper) {
        this.redisTemplate = redisTemplate;
        this.userInfoMapper = userInfoMapper;
        this.planMapper = planMapper;
    }

    @Override
    public Result getPageData(Long userid) {

        ReviewPageVo reviewPageVo = new ReviewPageVo();

        // 查找该用户是否为新注册的用户，即没有学习计划
        Plan plan = planMapper.selectOne(new QueryWrapper<Plan>().eq("userid", userid).eq("isCur", 1));

        String reviewNum = redisTemplate.opsForValue().get(String.valueOf(userid) + ":todayReviewNum");
        if (reviewNum == null) {
            reviewNum = "0";
        }

        if (plan == null) {
            reviewPageVo.setIsNotPlan("true");
            return Result.success(reviewPageVo);
        }

        List<Plan> allPlan = planMapper.selectList(new QueryWrapper<Plan>().eq("userid", userid));
        Integer allMens = 0;

        for (Plan item : allPlan) {
            allMens += item.getHadMem();
        }

        if (allMens == 0) {
            reviewPageVo.setIsNotMen("true");
        }

        // 获取每天复习的计划
        UserInfo userInfo = userInfoMapper.selectById(userid);
        System.out.println(userid);
        System.out.println(userInfo);
        reviewPageVo.setCount(userInfo.getReviewCnt() - Integer.parseInt(reviewNum));

        if (allMens.compareTo(userInfo.getReviewCnt()) < 0) {
            reviewPageVo.setCount(allMens);
        }

        // 今天计划是否已完成
        String isReview = redisTemplate.opsForValue().get(String.valueOf(userid) + ":isReview");
        reviewPageVo.setIsDone(isReview);

        return Result.success(reviewPageVo);
    }

}
