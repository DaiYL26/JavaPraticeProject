package com.example.login.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.login.dao.*;
import com.example.login.model.Plan;

import com.example.login.service.CollectService;

import com.example.login.vo.CollectPageVo;
import com.example.login.vo.Result;

import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CollectServiceImpl implements CollectService {

    private final CollectMapper collectMapper;

    private final PlanMapper planMapper;

    public CollectServiceImpl(PlanMapper planMapper,CollectMapper collectMapper) {
        this.planMapper = planMapper;
        this.collectMapper= collectMapper;

    }

    @Override
    public Map<String, Object> getCollect(Long userid, Integer dictID) {
//        HashMap<String, Object> resultMap = new HashMap<>();
//        List<Word> word = (collectMapper.getCollect(userid, dictID));
//
//        int size= word.size();
//
//        resultMap.put("data",word);
//        resultMap.put("size",size);
//        return resultMap;


//        ---------------------------------未加分页成功代码-------------------------------
        List<String> word = new ArrayList<>();
        HashMap<String, Object> resultMap = new HashMap<>();
        System.out.println(dictID);
        if(dictID==1)
        {
            collectMapper.getcollect1(userid, dictID).forEach(e -> word.add(e.getJson()));
        }
        else if(dictID==2)
        {
            collectMapper.getcollect2(userid, dictID).forEach(e -> word.add(e.getJson()));
        }
        else if(dictID==3)
        {
            collectMapper.getcollect3(userid, dictID).forEach(e -> word.add(e.getJson()));
        }
        int size= word.size();

        resultMap.put("data",word);
        resultMap.put("size",size);
        return resultMap;
//        ---------------------------------未加分页成功代码-------------------------------

    }

    @Override
    public Result getCollectStatus(Long userid) {

        Plan plan = planMapper.selectOne(new QueryWrapper<Plan>().eq("userid", userid).eq("isCur", 1));
        CollectPageVo collectPageVo=new CollectPageVo(plan.getDictID());

        return Result.success(collectPageVo);

    }


}
