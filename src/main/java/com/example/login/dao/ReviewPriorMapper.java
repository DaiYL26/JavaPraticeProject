package com.example.login.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.login.model.ReviewPrior;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
@TableName("review_prior")
public interface ReviewPriorMapper extends BaseMapper<ReviewPrior> {

    @Insert("insert into review_prior (userid, id, dictID, reviewCount, timestamp) values (#{userid}, #{id}, #{dictID}, #{reviewCount}, CURDATE())")
    Integer insertByDefaultDate(Long userid, Integer id, Integer dictID, Integer reviewCount);

    @Update("update review_prior set reviewCount = reviewCount - 1 where userid = #{userid} and id = #{id} and dictID = #{dictID}")
    Integer updateReviewCnt(Long userid, Integer id, Integer dictID);
}
