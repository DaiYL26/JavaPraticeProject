package com.example.login.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Word {
    private Integer id; //单词id
    private String word; //单词拼写
    private String json; //单词JSON数据
}
