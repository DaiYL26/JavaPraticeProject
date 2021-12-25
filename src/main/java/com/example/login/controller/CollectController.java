package com.example.login.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.login.service.CollectService;
import com.example.login.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@SaCheckLogin
@RestController
@RequestMapping("/collect")
public class CollectController {

    private final CollectService collectService;

    public CollectController(CollectService collectService) {
        this.collectService = collectService;
    }

    @PostMapping("/getCollect")
    public Map<String, Object> getCollect(Long userid, Integer dictID){
//        System.out.print("collectService.getCollect(userid, dictID)");
        return collectService.getCollect(userid, dictID);
    }

    @PostMapping("/getStatus")
    public Result getStatus(Long userid){
        return collectService.getCollectStatus(userid);
    }


}
