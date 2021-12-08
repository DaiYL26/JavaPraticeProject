package com.example.login.service.impl;

import com.example.login.dao.RecordMapper;
import com.example.login.dao.ReviewPriorMapper;
import com.example.login.model.Record;
import com.example.login.service.StatisticService;
import com.example.login.vo.Result;
import com.example.login.vo.StatisticVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class StatisticServiceImpl implements StatisticService {

    private final RecordMapper recordMapper;

    private final ReviewPriorMapper reviewPriorMapper;

    public StatisticServiceImpl(RecordMapper recordMapper, ReviewPriorMapper reviewPriorMapper) {
        this.recordMapper = recordMapper;
        this.reviewPriorMapper = reviewPriorMapper;
    }

    @Override
    public Result getStatisticData(Long userid, Integer days) {

        if (days == -1) {
            days = 365 * 10;
        }

        Integer recentCount = recordMapper.getRecentCount(userid, days);
        Integer priorCount = reviewPriorMapper.getRecentCount(userid, days);

        if (priorCount == null || recentCount == null) {
            return Result.fail(500, "系统出错啦");
        }

        ArrayList<StatisticVo> statisticVos = new ArrayList<>();

        statisticVos.add(new StatisticVo(recentCount, "记牢"));
        statisticVos.add(new StatisticVo(priorCount, "没记牢"));

        return Result.success(statisticVos);
    }
}
