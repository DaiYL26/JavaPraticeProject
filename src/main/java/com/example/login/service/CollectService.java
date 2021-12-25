package com.example.login.service;

import com.example.login.vo.Result;

import java.util.Map;

public interface CollectService {

    /**
     * 查询生词记录
     * @param userid
     */
    Map<String,Object> getCollect(Long userid, Integer dictID);

    Result getCollectStatus(Long userid);
}
