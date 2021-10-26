package com.example.login.service;

import com.example.login.vo.UserVo;

public interface UserService {

    public UserVo checkUser(String account, String pwd);

    public boolean resetPassword(String mail, String newPassword, String verifyCode);

    public boolean register(String registerMail, String registerPhone, String registerPassword, String registerCode);

    public boolean setVerifyCode(String mail);

    public boolean isAccountExist(String mail);

}
