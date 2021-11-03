package com.example.login.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {

    private Long id;

    private String mail;

    private String phone;

    private String nickName;
}
