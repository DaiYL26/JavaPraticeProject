package com.example.login.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewWordVo {

    private Boolean isPrior;
    private Integer id;
    private Integer dictID;
//    private Integer left;
    private Date timestamp;
    private String json;

}
