package com.example.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.dao.DictMapper;
import com.example.login.dao.PlanMapper;
import com.example.login.model.Dict;
import com.example.login.model.Plan;
import com.example.login.service.HomePageService;
import com.example.login.vo.HomePageVo;
import com.example.login.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class HomePageServiceImpl implements HomePageService {

    private final StringRedisTemplate redisTemplate;

    private final PlanMapper planMapper;

    private final DictMapper dictMapper;

    public HomePageServiceImpl(StringRedisTemplate redisTemplate, PlanMapper planMapper, DictMapper dictMapper) {
        this.redisTemplate = redisTemplate;
        this.planMapper = planMapper;
        this.dictMapper = dictMapper;
    }


    @Override
    public Result getHomePageData(Long userid) {
        HomePageVo homePageVo = new HomePageVo();
        String isDone = redisTemplate.opsForValue().get(String.valueOf(userid) + ":isDone");
        String todayNum = redisTemplate.opsForValue().get(String.valueOf(userid) + ":todayNum");
        if (todayNum == null) {
//            redisTemplate.opsForValue().set(String.valueOf(userid) + ":todayNum", "0");
//            redisTemplate.expireAt(String.valueOf(userid) + ":todayNum", )
//            redisTemplate
            todayNum = "0";
        }
        Plan plan = planMapper.selectOne(new QueryWrapper<Plan>().eq("userid", userid).eq("isCur", 1));
        if (plan == null) {
            return Result.success(null);
        }
        Dict dict = dictMapper.selectById(plan.getDictID());

        homePageVo.setIsDone(isDone);
        homePageVo.setCount(plan.getCount() - Integer.parseInt(todayNum));
        homePageVo.setDictName(dict.getDictName());
        homePageVo.setHadMem(plan.getHadMem());
        homePageVo.setTotalNum(dict.getTotalNum());
        homePageVo.setDictID(plan.getDictID());

        if (redisTemplate.opsForValue().get(userid + ":isDone") != null) {
            homePageVo.setCount(plan.getCount());
        }

        return Result.success(homePageVo);
    }

}
