package com.example.login.model;

import lombok.Data;

@Data
public class User {

    private Integer id;

    private String mail;

    private String phone;

    private String pwd;

    private String nickName;

    private Long lastLogin;

}
