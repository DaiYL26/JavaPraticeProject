package com.example.login.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("review_prior")
public class ReviewPrior {
    private Long userid;
    private Integer id;
    private Integer dictID;
    private Integer reviewCount;
    private Date timestamp;
}
