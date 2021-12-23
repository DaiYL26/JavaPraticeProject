package com.example.login.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dict")
public class Dict {
    @TableId(value = "dictID")
    private Integer dictID; //单词书id
    private String dictName; //单词书名
    private Integer totalNum; //单词书的词量总数
}
