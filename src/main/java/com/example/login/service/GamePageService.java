package com.example.login.service;

import com.example.login.game.model.Answer;
import com.example.login.vo.Result;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface GamePageService {

    Result getRankScore(Long id);

    Result getGameWords(String gameId);

    void saveGameResult();

    boolean initGameData(String gameId, Integer playerAId, Integer playerBId);

    boolean commitGame(String gameId, Integer playerAId, Integer playerBId);

    Boolean validateAnswer(Integer id,Answer answer);

}
