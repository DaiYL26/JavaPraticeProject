package com.example.login.game;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class GameKillerServer {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    public void addKillerTask(GameKiller gameKiller, Integer delay) {
        executorService.schedule(gameKiller, delay, TimeUnit.SECONDS);
        System.out.println("killer release");
    }

}
