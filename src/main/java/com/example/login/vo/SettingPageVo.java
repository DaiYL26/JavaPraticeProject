package com.example.login.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SettingPageVo {

    private Integer learnCount; //学习量
    private Integer reviewCount; //复习量

}
