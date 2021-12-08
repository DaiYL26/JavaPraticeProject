package com.example.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.dao.DictMapper;
import com.example.login.dao.PlanMapper;
import com.example.login.model.Dict;
import com.example.login.model.Plan;
import com.example.login.service.PlanService;
import com.example.login.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {

    private final PlanMapper planMapper;

    private final DictMapper dictMapper;

    public PlanServiceImpl(PlanMapper planMapper, DictMapper dictMapper) {
        this.planMapper = planMapper;
        this.dictMapper = dictMapper;
    }

    @Transactional
    @Override
    public Result updatePlan(Long userid, Integer dictID, Integer studyWord) {

        Plan curPlan = getCurPlan(userid);
        if (curPlan != null) {
            int initIsCur = planMapper.initIsCur(userid, curPlan.getDictID());
        }

        int updateIsCur = planMapper.updateIsCur(userid, dictID, studyWord);

        if (updateIsCur == 0) {
            return Result.fail(500, "系统错误");
        }

        return Result.success("修改成功");
    }

    private Plan getCurPlan(Long userid) {
        Plan plan = planMapper.selectOne(new QueryWrapper<Plan>().eq("userid", userid).eq("isCur", 1));
        return plan;
    }


    @Override
    public Result getLearnPlan(Long userid) {
        Plan plan = getCurPlan(userid);
        if (plan == null) {
            return Result.success("还没有学习计划");
        }
        Dict dict = dictMapper.selectById(plan.getDictID());
        return Result.success("当前正在学习 " + dict.getDictName() + " !");
    }

    @Override
    public Result getBooks() {
        List<Dict> dicts = dictMapper.selectList(new QueryWrapper<Dict>());
        return Result.success(dicts);
    }
}
