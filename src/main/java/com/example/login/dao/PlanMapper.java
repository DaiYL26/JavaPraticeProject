package com.example.login.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.login.model.Plan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
@TableName("plan")
public interface PlanMapper extends BaseMapper<Plan> {

    @Update("update plan set hadMem=hadMem+1 where userid=#{userid} and isCur=1")
    void updateHadMem(Long userid);

    //    增加
    @Update("update plan set count=#{studyword} where userid=#{userid} and isCur=1")
    int updateCount(Long userid,Integer studyword);

}
