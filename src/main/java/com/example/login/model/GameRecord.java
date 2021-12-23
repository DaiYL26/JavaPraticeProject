package com.example.login.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("game_record")
public class GameRecord {

    @TableId(value = "gameId")
    private String gameId;
    private Integer playerAId;
    private Integer playerAScore;
    private Integer playerBId;
    private Integer playerBScore;

}
