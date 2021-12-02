package com.example.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.config.RequestDataHelper;
import com.example.login.dao.*;
import com.example.login.model.Plan;
import com.example.login.service.LearnService;
import com.example.login.vo.Result;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class LearnServiceImpl implements LearnService {

    private final PlanMapper planMapper;

    private final DictMapper dictMapper;

    private final ReviewPriorMapper reviewPriorMapper;

    private final RecordMapper recordMapper;

    private final WordMapper wordMapper;

    private final StringRedisTemplate redisTemplate;

    public LearnServiceImpl(PlanMapper planMapper, DictMapper dictMapper, ReviewPriorMapper reviewPriorMapper, RecordMapper recordMapper, WordMapper wordMapper, StringRedisTemplate redisTemplate) {
        this.planMapper = planMapper;
        this.dictMapper = dictMapper;
        this.reviewPriorMapper = reviewPriorMapper;
        this.recordMapper = recordMapper;
        this.wordMapper = wordMapper;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public Result getWords(Long userid, Integer dictID, Integer count, Integer hadMem, Boolean isMore) {

        String isDone = redisTemplate.opsForValue().get(String.valueOf(userid) + ":isDone");

        if (isMore && isDone != null) {
            Plan plan = planMapper.selectOne(new QueryWrapper<Plan>().eq("userid", userid).eq("isCur", 1));
            count = plan.getCount();
            redisTemplate.opsForValue().setIfPresent(String.valueOf(userid) + ":todayNum", "0");
        }

        System.out.println(userid + " " + dictID + " " + count + " " + hadMem + " " + isMore);

        // 获取今日已学习数量
//        Integer todayNum = getHadLearnToday(userid);
//        count -= todayNum;
//        if (count < 0) {
//            count = 0;
//        }

        List<String> datas = new ArrayList<>();
        Integer totalNum = dictMapper.selectById(dictID).getTotalNum();

        // 获取今天学习的单词组
        ArrayList<Integer> ids = new ArrayList<>();
        for (int i = hadMem + 1; i <= hadMem + count && i <= totalNum; i++) {
            ids.add(i);
        }

        if (ids.size() == 0) {
            return Result.success(datas);
        }

        HashMap<String, Integer> dictChoice = new HashMap<>();
        dictChoice.put("dictID", dictID);
        RequestDataHelper.setRequestData(dictChoice);

        wordMapper.selectBatchIds(ids).forEach(e -> datas.add(e.getJson()));

        return Result.success(datas);
    }


    @Override
    public Boolean addRecord(Long userid, Integer dictID, Integer id, Boolean isMem) {

        if (!isMem) {
            reviewPriorMapper.insertByDefaultDate(userid, id, dictID, 3);
        } else {
            recordMapper.insertByDefaultDate(userid, id, dictID);
        }
        planMapper.updateHadMem(userid);
        Integer todayNum = getHadLearnToday(userid);
        return redisTemplate.opsForValue().setIfPresent(String.valueOf(userid) + ":todayNum", String.valueOf(todayNum + 1));
    }


    @Override
    public void setPlanStatus(Long userid) {
        Boolean aTrue = redisTemplate.opsForValue().setIfAbsent(String.valueOf(userid) + ":isDone", "true", 22, TimeUnit.HOURS);
        Boolean aBoolean = redisTemplate.opsForValue().setIfPresent(String.valueOf(userid) + ":todayNum", "0");
//        System.out.println(aTrue + " " + aBoolean);
//        System.out.println("Mark Done");
    }


    @Override
    public Integer getHadLearnToday(Long userid) {
        String todayNum = redisTemplate.opsForValue().get(String.valueOf(userid) + ":todayNum");
        if (todayNum == null) {
            redisTemplate.opsForValue().set(String.valueOf(userid) + ":todayNum", "0");
            todayNum = "0";
        }
        return Integer.parseInt(todayNum);
    }
}
