package com.example.login.service;

import com.example.login.vo.Result;

public interface LearnService {

    /**
     * 获取一组学习单词数据, 数量为count
     * @param dictID
     * @param count
     * @param hadMem
     * @return
     */
    Result getWords(Long userid, Integer dictID, Integer count, Integer hadMem, Boolean isMore);

    /**
     * 添加单词记忆记录
     * @param userid
     * @param id
     * @param isMem
     */
    Boolean addRecord(Long userid, Integer dictID, Integer id, Boolean isMem);

    /**
     * 设置状态，更新今日任务是否已完成
     * @param userid
     */
    void setPlanStatus(Long userid);

    /**
     * 获取今日以学习的数量
     * @param userid
     * @return
     */
    Integer getHadLearnToday(Long userid);

}
