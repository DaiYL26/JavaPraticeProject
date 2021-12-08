package com.example.login.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.login.service.ReviewPageService;
import com.example.login.service.ReviewService;
import com.example.login.vo.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SaCheckLogin
@RestController
@RequestMapping("/Review")
public class RestReviewController {

    private final ReviewService reviewService;

    private final ReviewPageService reviewPageService;

    public RestReviewController(ReviewService reviewService, ReviewPageService reviewPageService) {
        this.reviewService = reviewService;
        this.reviewPageService = reviewPageService;
    }

    @PostMapping("/getWords")
    public Result getReviewWords(Long userid, Integer count, Boolean isMore) {
        return reviewService.getReviewWords(userid, count, isMore);
    }

    @PostMapping("/getStatus")
    public Result getReviewStatus(Long userid) {
        return reviewPageService.getPageData(userid);
    }

    @PostMapping("/setReviewStatus")
    public void setReviewStatus(Long userid) {
        reviewService.setReviewStatus(userid);
    }

    @PostMapping("/updatePriorWord")
    public void updatePriorWord(Long userid, Integer id, Integer dictID, Boolean isRight) {
        reviewService.updatePriorWord(userid, id, dictID, isRight);
    }

}
