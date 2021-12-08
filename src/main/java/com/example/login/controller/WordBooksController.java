package com.example.login.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.login.service.PlanService;
import com.example.login.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SaCheckLogin
@RestController
@RequestMapping("/book")
public class WordBooksController {

    private final PlanService planService;

    public WordBooksController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping("/updateBook")
    public Result updateBook(Long userid, Integer dictID, Integer studyWord) {
        return planService.updatePlan(userid, dictID, studyWord);
    }

    @PostMapping("/getPlans")
    public Result getPlans(Long userid) {
        return planService.getLearnPlan(userid);
    }

    @PostMapping("/getBooks")
    public Result getBooks() {
        return planService.getBooks();
    }
}
