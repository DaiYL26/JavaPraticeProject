package com.example.login.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.login.model.ReviewPrior;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@TableName("review_prior")
public interface ReviewPriorMapper extends BaseMapper<ReviewPrior> {

    @Insert("insert into review_prior (userid, id, dictID, reviewCount, timestamp) values (#{userid}, #{id}, #{dictID}, #{reviewCount}, CURDATE())")
    Integer insertByDefaultDate(Long userid, Integer id, Integer dictID, Integer reviewCount);

}
