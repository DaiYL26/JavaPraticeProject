package com.example.login;

import com.example.login.game.GameServer;
import com.example.login.game.MatchServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class LoginApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(LoginApplication.class, args);

        MatchServer matchServer = context.getBean(MatchServer.class);
        matchServer.start();

        Thread gameServer = new Thread(() -> {
            GameServer server = context.getBean(GameServer.class);
            try {
                server.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        gameServer.start();

    }



}
