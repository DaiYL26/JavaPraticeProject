package com.example.login.service.impl;

import com.example.login.config.RequestDataHelper;
import com.example.login.dao.GameRecordMapper;
import com.example.login.dao.UserInfoMapper;
import com.example.login.dao.UserMapper;
import com.example.login.dao.WordMapper;
import com.example.login.game.ChannelManager;
import com.example.login.game.model.Answer;
import com.example.login.game.model.GameMessage;
import com.example.login.game.model.MessageType;
import com.example.login.model.GameRecord;
import com.example.login.model.User;
import com.example.login.model.UserInfo;
import com.example.login.model.Word;
import com.example.login.service.GamePageService;
import com.example.login.utils.JSONUtils;
import com.example.login.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class GamePageServiceImpl implements GamePageService {

    private final WordMapper wordMapper;

    private final StringRedisTemplate redisTemplate;

    private final ChannelManager channelManager;

    private final GameRecordMapper gameRecordMapper;

    private final UserInfoMapper userInfoMapper;

    private final UserMapper userMapper;

    public GamePageServiceImpl(WordMapper wordMapper, StringRedisTemplate redisTemplate, ChannelManager channelManager, GameRecordMapper gameRecordMapper, UserInfoMapper userInfoMapper, UserMapper userMapper) {
        this.wordMapper = wordMapper;
        this.redisTemplate = redisTemplate;
        this.channelManager = channelManager;
        this.gameRecordMapper = gameRecordMapper;
        this.userInfoMapper = userInfoMapper;
        this.userMapper = userMapper;
    }


    @Override
    public Result getRankScore(Long id) {

        try {
            ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();

            String score = opsForValue.get(id + ":score");
            String name = opsForValue.get(id + ":name");

            if (score == null) {
                UserInfo userInfo = userInfoMapper.selectById(id);
                opsForValue.set(id + ":score", userInfo.getRankScore().toString());
                score = userInfo.getRankScore().toString();
            }
            if (name == null) {
                User user = userMapper.selectById(id);
                opsForValue.set(id + ":name", user.getNickName());
            }

            return Result.success(Integer.parseInt(score));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.fail(500, "系统错误");
    }

    @Override
    public Result getGameWords(String gameId) {

        try {
            String json = (String) redisTemplate.opsForHash().get(gameId, "json");
            System.out.println(json);
            return Result.success(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.fail(500, "网络错误");
    }

    @Override
    public void saveGameResult() {

    }


    @Override
    public boolean initGameData(String gameId, Integer playerAId, Integer playerBId) {

        ObjectMapper objectMapper = null;

        try {

            objectMapper = JSONUtils.getObjectMapper();

            HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
            HashMap<String, Integer> dictMap = new HashMap<>();
            dictMap.put("dictID", 1);
            RequestDataHelper.setRequestData(dictMap);

            ArrayList<Integer> ids = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                int wordRank = ThreadLocalRandom.current().nextInt(1, 3500);
                ids.add(wordRank);
            }

            List<Word> words = wordMapper.selectBatchIds(ids);

            for (Word word : words) {
                opsForHash.put(gameId, "word:" + word.getId(), word.getWord());
            }

            opsForHash.put(gameId, playerAId.toString(), "0");
            opsForHash.put(gameId, playerBId.toString(), "0");

            String json = objectMapper.writeValueAsString(words);
            opsForHash.put(gameId, "json", json);

            log.info("------->" + gameId);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectMapper != null) {
                JSONUtils.returnObjectMapper(objectMapper);
            }
        }

        return false;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean commitGame(String gameId, Integer playerAId, Integer playerBId) {

        ObjectMapper objectMapper = null;
        try {
            objectMapper = JSONUtils.getObjectMapper();
            HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
            ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();

            String scoreA = (String) opsForHash.get(gameId, playerAId.toString());
            String scoreB = (String) opsForHash.get(gameId, playerBId.toString());

            if (scoreB == null || scoreA == null) {
                return false;
            }

            int playerAScore = Integer.parseInt(scoreA);
            int playerBScore = Integer.parseInt(scoreB);

            GameRecord gameRecord = new GameRecord(gameId, playerAId, playerAScore, playerBId, playerBScore);
            gameRecordMapper.insert(gameRecord);

            if (playerAScore > playerBScore) {
                System.out.println(playerAId);
                userInfoMapper.updateRankScore(playerAId, 10);
                opsForValue.increment(playerAId + ":score", 10);
            } else if (playerBScore > playerAScore){
                System.out.println(playerBId);
                userInfoMapper.updateRankScore(playerBId, 10);
                opsForValue.increment(playerBId + ":score", 10);
            } else {
                userInfoMapper.updateRankScore(playerAId, 5);
                userInfoMapper.updateRankScore(playerBId, 5);
                opsForValue.increment(playerAId + ":score", 5);
                opsForValue.increment(playerBId + ":score", 5);
            }

            GameMessage messageA = new GameMessage(playerAId, MessageType.COMMIT, scoreA + ";" + scoreB);
            GameMessage messageB = new GameMessage(playerBId, MessageType.COMMIT, scoreB + ";" + scoreA);

            Channel channelA = channelManager.getChannelById(playerAId).channel();
            Channel channelB = channelManager.getChannelById(playerBId).channel();

            channelA.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(messageA)));
            channelB.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(messageB)));

            redisTemplate.delete(gameId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectMapper != null) {
                JSONUtils.returnObjectMapper(objectMapper);
            }
        }

        return false;
    }

    @Override
    public Boolean validateAnswer(Integer id, Answer answer) {

        try {
            HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
            Boolean aBoolean = redisTemplate.hasKey(answer.getGameId());
            System.out.println(aBoolean);
            if (aBoolean == null || aBoolean.equals(Boolean.FALSE) || answer.getSpelling() == null) {
                return null;
            }

            String res = (String) opsForHash.get(answer.getGameId(), "word:" + answer.getWordRank());
            System.out.println(res + "----->" + "word:" + answer.getWordRank());
            if (res == null) {
                return null;
            }

            boolean equals = res.equals(answer.getSpelling());

            if (equals) {
                opsForHash.increment(answer.getGameId(), id.toString(), 1);
            }

            return equals;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
