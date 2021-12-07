package com.example.login.service;


import com.example.login.vo.Result;

public interface SettingService {

    /**
     * 修改设置
     * @param userid
     * @param username
     * @param studyword
     * @param reviewCnt
     */
    Result updateRecord(Long userid, String username, Integer studyword, Integer reviewCnt);

    /**
     * 获取系统设置
     * @param userid
     * @return
     */
    Result getSettingStatus(Long userid);
}
