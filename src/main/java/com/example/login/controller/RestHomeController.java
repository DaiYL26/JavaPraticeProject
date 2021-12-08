package com.example.login.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.login.service.HomePageService;
import com.example.login.service.LearnService;
import com.example.login.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SaCheckLogin
@RestController
@RequestMapping("/home")
public class RestHomeController {

    private final LearnService learnService;

    private final HomePageService homePageService;

    public RestHomeController(LearnService learnService, HomePageService homePageService) {
        this.learnService = learnService;
        this.homePageService = homePageService;
    }

    @PostMapping("/getStatus")
    public Result getStatus(Long userid) {
        Result homePageData = homePageService.getHomePageData(userid);

        return homePageData;
    }

    @PostMapping("/getWords")
    public Result getWords(Long userid, Integer dictID, Integer count, Integer hadMem, Boolean isMore) {
        System.out.println(userid + " " + dictID + " " + count + " " + hadMem + " " + isMore);
        Result words = learnService.getWords(userid, dictID, count, hadMem, isMore);

        return words;
    }

    @PostMapping("/addRecord")
    public Boolean addRecord(Long userid, Integer dictID, Integer id, Boolean isMem) {
        return learnService.addRecord(userid, dictID, id, isMem);
    }

    @PostMapping("/setPlanStatus")
    public void setPlanStatus(Long userid) {
        learnService.setPlanStatus(userid);
    }

    @CrossOrigin
    @PostMapping("/getSlides")
    public Result getSlide(Long userid, Integer count) {
        return learnService.getRandomWords(userid, count);
    }

}
