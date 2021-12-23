package com.example.login.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OpponentInfo {

    private String id;
    private String name;
    private String avatar;
    private String rankScore;

}
