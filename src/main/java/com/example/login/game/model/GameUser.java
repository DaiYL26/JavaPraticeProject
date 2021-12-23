package com.example.login.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameUser {

    private Integer id;
    private String name;
    private Integer score;
    private Integer waitTime = 0;

    public void increment() {
        waitTime += 1;
    }

}
