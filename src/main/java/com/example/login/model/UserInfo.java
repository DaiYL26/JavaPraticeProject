package com.example.login.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_info")
public class UserInfo {

    @TableId("userid")
    private Long userid;
    private String avatar;
    private Integer reviewCnt;

}
