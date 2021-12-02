package com.example.login.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("record")
public class Record {
    private Long userid;
    private Integer id;
    private Date date;
    private Integer dictID;
}
