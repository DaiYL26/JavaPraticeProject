package com.example.login.game;

import com.example.login.game.model.GameUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

@Slf4j
@Component
public class MatchPool {


    private final GameManager gameManager;

    private final Comparator<GameUser> comparator = new Comparator<GameUser>() {
        @Override
        public int compare(GameUser o1, GameUser o2) {
            return o1.getScore() - o2.getScore();
        }
    };

    private final PriorityBlockingQueue<GameUser> users = new PriorityBlockingQueue<>(10, comparator);

    private final List<GameUser> residue = new ArrayList<>();

    public MatchPool(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void add(GameUser gameUser, ChannelHandlerContext ctx) {
        users.add(gameUser);
        gameManager.addPlayer(gameUser, ctx);
    }

    public void remove(Integer id) {
        boolean b = users.removeIf(e -> e.getId().equals(id));
        System.out.println(b);
    }

    public void match() throws JsonProcessingException {

        users.forEach(GameUser::increment);

        while (users.size() >= 2) {

            GameUser playerA = users.poll();
            GameUser playerB = users.poll();

            int delta = Math.min(playerA.getWaitTime() * 50, playerB.getWaitTime() * 50);

            if (playerA.getScore() + delta >= playerB.getScore()) {
                // 匹配成功
                gameManager.startGame(playerA, playerB);

                log.info("Match success : " + playerA + " vs " + playerB);
            } else {
                residue.add(playerA);
                users.add(playerB);
//                residue.add(playerB);
            }

        }

        users.addAll(residue);
        residue.clear();

    }

}
