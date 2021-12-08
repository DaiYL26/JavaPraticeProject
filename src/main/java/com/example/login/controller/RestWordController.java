package com.example.login.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.login.model.Word;
import com.example.login.service.SearchService;
import com.example.login.vo.Result;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@SaCheckLogin
@RestController
public class RestWordController {

    private final SearchService searchService;

    public RestWordController(SearchService searchService) {
        this.searchService = searchService;
    }


    @PostMapping("/query/en")
    public Result query(String word) throws TException {
        System.out.println(word);
        String data = searchService.queryForEn(word);
        System.out.println(data);
        return Result.success(data);
    }


    @PostMapping("/query/zh")
    public Result query(String mean, Integer limit) throws TException {
        System.out.println(mean);
        String data = searchService.queryForZh(mean, limit);
        System.out.println(data);
        return Result.success(data);
    }


}
