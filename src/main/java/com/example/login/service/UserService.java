package com.example.login.service;

import com.example.login.vo.UserVo;

public interface UserService {

    public UserVo checkUser(String account, String pwd);

    public boolean resetPassword(String mail, String newPassword, String verifyCode);

    public boolean register(String registerMail, String registerPhone, String registerPassword, String nickName, String registerCode);

    public boolean setVerifyCode(String mail);

    public boolean isAccountExist(String mail);

    public UserVo getUserInfo(Integer id);

    public UserVo checkCodeLogin(String mail, String code);

}
