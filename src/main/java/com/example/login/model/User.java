package com.example.login.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class User {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String mail;

    private String phone;

    private String pwd;

    private String nickName;

    private Long lastLogin;

}
