package com.example.login.game;


import com.example.login.game.model.Answer;
import com.example.login.game.model.GameMessage;
import com.example.login.game.model.GameUser;
import com.example.login.game.model.MessageType;
import com.example.login.service.GamePageService;
import com.example.login.utils.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class GameManager {

    private final GamePageService gamePageService;

    private final ChannelManager channelManager;

    private final ApplicationContext context;

    private final GameKillerServer gameKillerServer = new GameKillerServer();

    private final ConcurrentHashMap<Integer, Integer> opponentsMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, String> idMapGameId = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String , Integer> gamedReady = new ConcurrentHashMap<>();

    public GameManager(ChannelManager channelManager, GamePageService gamePageService, ApplicationContext context) {
        this.channelManager = channelManager;
        this.gamePageService = gamePageService;
        this.context = context;
    }

    public Integer getOpponent(Integer id) {
        return opponentsMap.get(id);
    }

    public void removeChannel(Integer id) {
        channelManager.removeChannel(id);
    }

    public void removeChannel(ChannelHandlerContext ctx) {
        channelManager.removeChannel(ctx);
    }

    public void sizeInfo() {
        System.out.println(opponentsMap.size() + " " + idMapGameId.size() + " " + gamedReady.size() );
        channelManager.sizeInfo();
    }
    public Integer getIdByChannel(ChannelHandlerContext ctx) {
        return channelManager.getIdByChannel(ctx);
    }

    public ChannelHandlerContext getChannelById(Integer id) {
        return channelManager.getChannelById(id);
    }

    /**
     * 添加玩家
     * @param gameUser
     * @param ctx
     */
    public void addPlayer(GameUser gameUser, ChannelHandlerContext ctx) {
        channelManager.addPlayer(gameUser.getId(), ctx);
    }

    /**
     * 开始游戏，并准备游戏数据
     * @param playerA
     * @param playerB
     * @return
     */
    public boolean startGame(GameUser playerA, GameUser playerB) {
        Integer playerAId = playerA.getId();
        Integer playerBId = playerB.getId();
        ObjectMapper objectMapper = null;

        try {
            objectMapper = JSONUtils.getObjectMapper();
            opponentsMap.put(playerAId, playerBId);
            opponentsMap.put(playerBId, playerAId);

            long time = new Date().getTime();
            String gameId = Long.toString(time);
            gamedReady.put(gameId, 0);

            idMapGameId.put(playerAId, gameId);
            idMapGameId.put(playerBId, gameId);

            Channel channelA = channelManager.getChannelById(playerA.getId()).channel();
            Channel channelB = channelManager.getChannelById(playerB.getId()).channel();
            // 准备对局数据
            boolean isInitGame = gamePageService.initGameData(gameId, playerAId, playerBId);
            if (isInitGame) {
                GameMessage InfoA = new GameMessage(playerA.getId(), MessageType.MATCH_SUCCESS, objectMapper.writeValueAsString(playerA) + ";" + gameId);
                GameMessage InfoB = new GameMessage(playerB.getId(), MessageType.MATCH_SUCCESS, objectMapper.writeValueAsString(playerB) + ";" + gameId);

                channelA.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(InfoB)));
                channelB.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(InfoA)));
            } else {
                channelA.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(new GameMessage(-1, MessageType.ERROR, "game init failed"))));
                channelB.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(new GameMessage(-1, MessageType.ERROR, "game init failed"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (objectMapper != null) {
                JSONUtils.returnObjectMapper(objectMapper);
            }
        }

        return true;
    }

    private void releaseGameKiller(String gameId, Integer playerAId, Integer playerBId) {
        GameKiller gameKiller = context.getBean(GameKiller.class);
        gameKiller.setGameId(gameId);
        gameKiller.setPlayerA(playerAId);
        gameKiller.setPlayerB(playerBId);
//        System.out.println(gameKiller);

        gameKillerServer.addKillerTask(gameKiller, 60);
        log.info(gameId + " : killer release");
    }

    public void setGamedReady(ChannelHandlerContext ctx) {
        ObjectMapper objectMapper = null;
        try {
            objectMapper = JSONUtils.getObjectMapper();
            Integer playerId = channelManager.getIdByChannel(ctx);
            String gameId = idMapGameId.get(playerId);
            Integer readyNum = 0;
            synchronized (this) {
                readyNum = gamedReady.get(gameId);
                gamedReady.put(gameId, readyNum + 1);
                readyNum = gamedReady.get(gameId);
            }
            if (readyNum == 2) {
                GameMessage message = new GameMessage(-1, MessageType.START, null);
                Integer opponent = opponentsMap.get(playerId);
                Channel opponentChannel = channelManager.getChannelById(opponent).channel();
                // 通知对战正式开始
                String frame = objectMapper.writeValueAsString(message);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(frame));
                opponentChannel.writeAndFlush(new TextWebSocketFrame(frame));

                // 开启倒计时
                releaseGameKiller(gameId, playerId, opponent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectMapper != null) {
                JSONUtils.returnObjectMapper(objectMapper);
            }
        }

    }

    /**
     * 游戏结束，移除用户的缓存数据
     * @param gameId
     * @param playerA
     * @param playerB
     */
    public void remove(String gameId, Integer playerA, Integer playerB) {
        opponentsMap.remove(playerA);
        opponentsMap.remove(playerB);
        idMapGameId.remove(playerA);
        idMapGameId.remove(playerB);
        gamedReady.remove(gameId);

        ChannelHandlerContext channelA = channelManager.getChannelById(playerA);
        ChannelHandlerContext channelB = channelManager.getChannelById(playerB);

        clearChannel(channelA);
        clearChannel(channelB);
    }

    /**
     * 清楚channel的缓存
     * @param ctx 相应channelContext
     */
    public void clearChannel(ChannelHandlerContext ctx) {
        if (!ctx.isRemoved()) {
            ctx.close();
        }
        channelManager.removePlayer(ctx);
    }

    /**
     * 验证答案
     * @param id 用户id
     * @param answer 用户的答案
     */
    public void validateAnswer(Integer id, Answer answer) {

        ObjectMapper objectMapper = null;

        try {
            Boolean isTrue = gamePageService.validateAnswer(id, answer);
            objectMapper = JSONUtils.getObjectMapper();
            if (isTrue) {
                Integer opponent = opponentsMap.get(id);
                GameMessage opponentFrame = new GameMessage(-1, MessageType.CORRECT, "false");
                GameMessage frame = new GameMessage(-1, MessageType.CORRECT, "true");
                channelManager.getChannelById(opponent).channel().writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(opponentFrame)));
                channelManager.getChannelById(id).channel().writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(frame)));
            } else {
                GameMessage message = new GameMessage(-1, MessageType.WRONG, "");
                channelManager.getChannelById(id).channel().writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(message)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectMapper != null) {
                JSONUtils.returnObjectMapper(objectMapper);
            }
        }
    }
}
