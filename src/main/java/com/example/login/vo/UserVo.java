package com.example.login.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {

    private Long id; //用户id

    private String mail; //用户邮箱

    private String phone; //用户手机号

    private String nickName; //用户昵称
}
