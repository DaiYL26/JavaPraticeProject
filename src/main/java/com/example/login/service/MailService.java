package com.example.login.service;

public interface MailService {

    public int sendVerifyCode(String toMail);

    public boolean verify(String mail, String verifyCode);

    public boolean isExist(String mail);

}
