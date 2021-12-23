package com.example.login.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MatchServer extends Thread {

    private final MatchPool matchPool;

    public MatchServer(MatchPool matchPool) {
        this.matchPool = matchPool;
    }

    @Override
    public void run() {
        while (true) {
            try {
                matchPool.match();
                Thread.sleep(1000);
            } catch (JsonProcessingException e) {
                log.error("JSON序列化出错：" + e.getMessage());
                e.printStackTrace();
            } catch (InterruptedException e) {
                log.error("匹配服务出错: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
