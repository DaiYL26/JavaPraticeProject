package com.example.login.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.login.model.Word;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface WordMapper extends BaseMapper<Word> {
}
