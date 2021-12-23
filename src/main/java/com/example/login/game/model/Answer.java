package com.example.login.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Answer {

    private Integer wordRank;
    private String spelling;
    private String gameId;

}
