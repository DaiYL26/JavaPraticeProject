package com.example.login.service;

import com.example.login.vo.Result;

public interface PlanService {

    /**
     * 修改设置
     * @param userid
     * @param dictID
     * @param studyWord
     */
    Result updatePlan(Long userid, Integer dictID, Integer studyWord);


    /**
     * 获取学习计划
     * @param userid
     * @return
     */
    Result getLearnPlan(Long userid);

    Result getBooks();
}
