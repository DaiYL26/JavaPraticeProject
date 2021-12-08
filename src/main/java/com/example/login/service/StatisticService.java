package com.example.login.service;

import com.example.login.vo.Result;

public interface StatisticService {

    Result getStatisticData(Long userid, Integer days);

}
