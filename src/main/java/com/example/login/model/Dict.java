package com.example.login.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dict")
public class Dict {
    @TableId(value = "dictID")
    private Integer dictID;
    private String dictName;
    private Integer totalNum;
}
