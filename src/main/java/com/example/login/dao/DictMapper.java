package com.example.login.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.login.model.Dict;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@TableName("dict")
public interface DictMapper extends BaseMapper<Dict> {
}
