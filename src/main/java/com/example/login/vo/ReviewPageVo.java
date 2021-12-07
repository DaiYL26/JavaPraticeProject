package com.example.login.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewPageVo {

    private String isNotPlan;
    private String isNotMen;
    private Integer count;
    private String isDone;

}
