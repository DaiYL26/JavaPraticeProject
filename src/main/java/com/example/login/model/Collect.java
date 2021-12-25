package com.example.login.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("Collect")
public class Collect {
    private Long userid;
    private Integer dictID;
    private Integer id;
}
