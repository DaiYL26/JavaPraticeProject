package com.example.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.config.RequestDataHelper;
import com.example.login.dao.*;
import com.example.login.model.Plan;
import com.example.login.service.LearnService;
import com.example.login.utils.TimeUtils;
import com.example.login.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

//        if (isMore && isDone != null) {
//            Plan plan = planMapper.selectOne(new QueryWrapper<Plan>().eq("userid", userid).eq("isCur", 1));
//            count = plan.getCount();
//            redisTemplate.opsForValue().setIfPresent(String.valueOf(userid) + ":todayNum", "0");
//            redisTemplate.expireAt(String.valueOf(userid) + ":todayNum", TimeUtils.getNextDayTimestamp());
//        }

//        String todayNum1 = redisTemplate.opsForValue().get(String.valueOf(userid) + ":todayNum");
//
//        if (isDone == null && todayNum1 == null) {
//            redisTemplate.opsForValue().set(String.valueOf(userid) + ":todayNum", "0");
//            redisTemplate.expireAt(String.valueOf(userid) + ":todayNum", TimeUtils.getNextDayTimestamp());
//        }

//        System.out.println(userid + " " + dictID + " " + count + " " + hadMem + " " + isMore);
//        log.info(new Date().toString() +  userid + " " + dictID + " " + count + " " + hadMem + " " + isMore);
        // ???????????????????????????
        getHadLearnToday(userid);
//        Integer todayNum = getHadLearnToday(userid);
//        count -= todayNum;
//        if (count < 0) {
//            count = 0;
//        }

        List<String> datas = new ArrayList<>();
        Integer totalNum = dictMapper.selectById(dictID).getTotalNum();

        // ??????????????????????????????
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


    @Transactional
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
        Boolean aTrue = redisTemplate.opsForValue().setIfAbsent(String.valueOf(userid) + ":isDone", "true");
        redisTemplate.expireAt(String.valueOf(userid) + ":isDone", TimeUtils.getNextDayTimestamp());
        Boolean aBoolean = redisTemplate.opsForValue().setIfPresent(String.valueOf(userid) + ":todayNum", "0");
        redisTemplate.expireAt(String.valueOf(userid) + ":todayNum", TimeUtils.getNextDayTimestamp());
//        System.out.println(aTrue + " " + aBoolean);
//        System.out.println("Mark Done");
    }


    @Override
    public Integer getHadLearnToday(Long userid) {
        String todayNum = redisTemplate.opsForValue().get(String.valueOf(userid) + ":todayNum");
        if (todayNum == null) {
            redisTemplate.opsForValue().set(String.valueOf(userid) + ":todayNum", "0");
            redisTemplate.expireAt(String.valueOf(userid) + ":todayNum", TimeUtils.getNextDayTimestamp());
            todayNum = "0";
        }
        return Integer.parseInt(todayNum);
    }

    public Result getRandomWords(Long userid, Integer count) {
        Plan plan = planMapper.selectOne(new QueryWrapper<Plan>().eq("userid", userid).eq("isCUr", 1));

        Integer dictID = 1;

        if (plan != null) {
            dictID = plan.getDictID();
        }

        int bound = dictMapper.selectById(dictID).getTotalNum();

        HashMap<String, Integer> dictMap = new HashMap<>();
        dictMap.put("dictID", dictID);
        RequestDataHelper.setRequestData(dictMap);

        ArrayList<Integer> ids = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int id = ThreadLocalRandom.current().nextInt(1, bound);
            ids.add(id);
        }

        ArrayList<String> datas = new ArrayList<>();

        wordMapper.selectBatchIds(ids).forEach(e -> datas.add(e.getJson()));

        return Result.success(datas);
    }
}
