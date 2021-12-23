package com.example.login.game;


import com.example.login.service.GamePageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope("prototype")
public class GameKiller extends Thread {

    private String gameId;

    private Integer playerA;

    private Integer playerB;

    private final GamePageService gamePageService;

    private final ApplicationContext context;

    public GameKiller(GamePageService gamePageService, ApplicationContext context) {
        this.gamePageService = gamePageService;
        this.context = context;
    }

    @Override
    public void run() {
        gamePageService.commitGame(gameId, playerA, playerB);
        GameManager gameManager = context.getBean(GameManager.class);
        gameManager.remove(gameId, playerA, playerB);
        log.info(gameId + " had been committed");
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setPlayerA(Integer playerA) {
        this.playerA = playerA;
    }

    public void setPlayerB(Integer playerB) {
        this.playerB = playerB;
    }
}
