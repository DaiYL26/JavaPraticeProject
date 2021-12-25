package com.example.login.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.login.service.GamePageService;
import com.example.login.vo.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin
@SaCheckLogin
@RestController
@RequestMapping("/game")
public class GamePageController {

    private final GamePageService gamePageService;

    public GamePageController(GamePageService gamePageService) {
        this.gamePageService = gamePageService;
    }

    @PostMapping("/getGameWords")
    public Result getGameWord(String gameId) {
        return gamePageService.getGameWords(gameId);
    }

    @PostMapping("/getStatus")
    public Result getStatus(Long id) {
        return gamePageService.getRankScore(id);
    }

}
