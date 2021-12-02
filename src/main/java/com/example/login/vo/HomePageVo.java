package com.example.login.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HomePageVo {
    private String isDone;
    private String dictName;
    private Integer count;
    private Integer hadMem;
    private Integer totalNum;
    private Integer dictID;
}
