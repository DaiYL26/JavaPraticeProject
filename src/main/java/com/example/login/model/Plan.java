package com.example.login.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("plan")
public class Plan {
    private Long userid;
    private Integer dictID;
    private Integer count;
    private Integer hadMem;
}
