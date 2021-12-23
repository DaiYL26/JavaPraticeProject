package com.example.login.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.login.service.StatisticService;
import com.example.login.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@SaCheckLogin
@RestController
@RequestMapping("/statistic")
public class RestStatisticController {

    private final StatisticService statisticService;

    public RestStatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @PostMapping("/getRecentData")
    public Result getRecentData(Long userid, Integer days) {
        log.info(userid + " at the page settings");
        return statisticService.getStatisticData(userid, days);
    }

}
