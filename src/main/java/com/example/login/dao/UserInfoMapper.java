package com.example.login.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.login.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
@TableName("user_info")
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    @Update("update user_info set reviewCnt=#{reviewCnt} where userid=#{userid}")
    int updateReviewCnt(Long userid,Integer reviewCnt);

    @Update("update user_info set rankScore = rankScore + #{cnt} where userid=#{userid}")
    int updateRankScore(Integer userid,Integer cnt);
}
