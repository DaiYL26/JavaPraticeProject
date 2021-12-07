package com.example.login.service;

import com.example.login.vo.Result;

public interface ReviewService {

    /**
     * 获取复习单词
     * @param userid
     * @param count
     * @param isMore
     * @return
     */
    Result getReviewWords(Long userid, Integer count, Boolean isMore);

    /**
     * 设置今天复习状态
     * @param userid
     */
    void setReviewStatus(Long userid);

    /**
     * 更新今天复习数量
     * @param userid
     * @param id
     * @param dictID
     * @param isRight
     */
    void updatePriorWord(Long userid, Integer id, Integer dictID, Boolean isRight);

}
