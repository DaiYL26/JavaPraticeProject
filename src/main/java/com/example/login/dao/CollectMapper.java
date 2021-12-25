package com.example.login.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.login.model.Collect;
import com.example.login.model.Word;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@TableName("collect")
public interface CollectMapper extends BaseMapper<Collect>{

    @Insert("insert into collect (userid, id, dictID) values (#{userid}, #{id}, #{dictID})")
    Integer insertCollect(Long userid, Integer id, Integer dictID);

    @Delete("delete from collect where userid=#{userid} and id=#{id} and dictID=#{dictID}")
    Integer delCollect(Long userid, Integer id, Integer dictID);

    @Select("select count(*) from collect where userid = #{userid} and id=#{id} and dictID=#{dictID}")
    Integer selCollect(Long userid, Integer id, Integer dictID);

    @Select("select * from cet4 where id=any(select id from collect where userid=#{userid} and dictID=1)")
    List<Word> getcollect1(Long userid, Integer dictID);

    @Select("select * from cet6 where id=any(select id from collect where userid=#{userid} and dictID=2)")
    List<Word> getcollect2(Long userid, Integer dictID);

    @Select("select * from gk where id=any(select id from collect where userid=#{userid} and dictID=3)")
    List<Word> getcollect3(Long userid, Integer dictID);
}