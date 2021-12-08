package com.example.login.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.login.model.Plan;
import org.apache.ibatis.annotations.Insert;
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

    @Update("update plan set isCur = 0 where userid=#{userid} and dictID=#{dictID}")
    int initIsCur(Long userid, Integer dictID);

    @Insert("insert into plan(userid, dictID, count, hadMem, isCur) value(#{userid}, #{dictID}, #{studyWord}, 0, 1) on duplicate key update isCur = 1 , count=#{studyWord};")
    int updateIsCur(Long userid,Integer dictID,Integer studyWord);

//    @Insert("insert into plan(userid, dictID, count, hadMem, isCur) values (#{userid}, #{dictID}, #{studyWord}, 0, 1)")
//    int insertPlan(Long userid,Integer dictID,Integer studyWord);

}
