package com.example.login.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.login.model.Record;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
@TableName("record")
public interface RecordMapper extends BaseMapper<Record> {

    @Insert("insert into record (userid, id, dictID, timestamp) values (#{userid}, #{id}, #{dictID}, CURDATE())")
    Integer insertByDefaultDate(Long userid, Integer id, Integer dictID);

    @Select("select count(*) from record where userid = #{userid} and TO_DAYS(NOW()) - TO_DAYS(timestamp) <= #{days}")
    Integer getRecentCount(Long userid, Integer days);

}
