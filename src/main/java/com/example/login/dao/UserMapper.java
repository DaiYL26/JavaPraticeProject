package com.example.login.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.login.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@TableName("user")
public interface UserMapper extends BaseMapper<User> {

    public User checkUser(String account, String password);

    public int updateAccount(String mail, String password);

    public int registerAccount(String mail, String phone, String password, String nickName);

}
