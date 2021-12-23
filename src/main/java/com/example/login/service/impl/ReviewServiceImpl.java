package com.example.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.config.RequestDataHelper;
import com.example.login.dao.RecordMapper;
import com.example.login.dao.ReviewPriorMapper;
import com.example.login.dao.WordMapper;
import com.example.login.model.Record;
import com.example.login.model.ReviewPrior;
import com.example.login.model.Word;
import com.example.login.service.ReviewService;
import com.example.login.utils.TimeUtils;
import com.example.login.vo.Result;
import com.example.login.vo.ReviewWordVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewPriorMapper reviewPriorMapper;

    private final RecordMapper recordMapper;

    private final StringRedisTemplate redisTemplate;

    private final WordMapper wordMapper;

    public ReviewServiceImpl(ReviewPriorMapper reviewPriorMapper, RecordMapper recordMapper, StringRedisTemplate redisTemplate, WordMapper wordMapper) {
        this.reviewPriorMapper = reviewPriorMapper;
        this.recordMapper = recordMapper;
        this.redisTemplate = redisTemplate;
        this.wordMapper = wordMapper;
    }

    private ArrayList<ReviewWordVo> getPrior(List<ReviewPrior> priors) {
        HashMap<Integer, List<Integer>> dict2ids = new HashMap<>();
        ArrayList<ReviewWordVo> reviewWordVos = new ArrayList<>();

        priors.forEach(e -> {
            if (!dict2ids.containsKey(e.getDictID())) {
                dict2ids.put(e.getDictID(), new ArrayList<Integer>());
            }
            dict2ids.get(e.getDictID()).add(e.getId());
        });

        for (Integer dictID : dict2ids.keySet()) {
            HashMap<String, Integer> helperMap = new HashMap<>();
            helperMap.put("dictID", dictID);
            RequestDataHelper.setRequestData(helperMap);

            List<Word> words = wordMapper.selectBatchIds(dict2ids.get(dictID));

            words.forEach(e -> {
                ReviewWordVo reviewWordVo = new ReviewWordVo();
                reviewWordVo.setJson(e.getJson());
                reviewWordVo.setDictID(dictID);
                reviewWordVo.setIsPrior(true);
                reviewWordVo.setId(e.getId());
//                    reviewWordVo.setLeft();
                reviewWordVos.add(reviewWordVo);
            });
        }

        return reviewWordVos;
    }

    private ArrayList<ReviewWordVo> getNormal(List<Record> records, Integer count) {
        Collections.shuffle(records);
        HashMap<Integer, List<Integer>> dict2ids = new HashMap<>();
        ArrayList<ReviewWordVo> wordVos = new ArrayList<>();

        for (int i = 0; i < Math.min(count, records.size()); i ++) {
            Record record = records.get(i);
            if (!dict2ids.containsKey(record.getDictID())) {
                dict2ids.put(record.getDictID(), new ArrayList<Integer>());
            }
            dict2ids.get(record.getDictID()).add(record.getId());
        }

        for (Integer dictID : dict2ids.keySet()) {
            HashMap<String, Integer> helperMap = new HashMap<>();
            helperMap.put("dictID", dictID);
            RequestDataHelper.setRequestData(helperMap);

            List<Word> words = wordMapper.selectBatchIds(dict2ids.get(dictID));

            words.forEach(e -> {
                ReviewWordVo wordVo = new ReviewWordVo();
                wordVo.setJson(e.getJson());
                wordVo.setDictID(dictID);
                wordVo.setIsPrior(false);
                wordVo.setId(e.getId());

                wordVos.add(wordVo);
            });
        }

        return wordVos;
    }

    @Override
    public Result getReviewWords(Long userid, Integer count, Boolean isMore) {

        String todayNum = redisTemplate.opsForValue().get(String.valueOf(userid) + ":todayReviewNum");
        if (todayNum == null) {
            redisTemplate.opsForValue().set(String.valueOf(userid) + ":todayReviewNum", "0");
            redisTemplate.expireAt(String.valueOf(userid) + ":todayReviewNum", TimeUtils.getNextDayTimestamp());
        } else if (!isMore){
            count -= Integer.parseInt(todayNum);
        }

        List<ReviewPrior> priors = reviewPriorMapper.selectList(new QueryWrapper<ReviewPrior>().eq("userid", userid)
                .and(i -> i.ge("TO_DAYS(NOW()) - TO_DAYS(timestamp)", 1)).orderByDesc("timestamp").last("limit " + count));

        if (priors.size() == count) {
            ArrayList<ReviewWordVo> wordVos = getPrior(priors);
            Collections.shuffle(wordVos);
            return Result.success(wordVos);
        } else if (priors.size() < count) {
            ArrayList<ReviewWordVo> wordVos = getPrior(priors);
            List<Record> records = recordMapper.selectList(new QueryWrapper<Record>().eq("userid", userid)
                    .and(i -> i.ge("TO_DAYS(NOW()) - TO_DAYS(timestamp)", 1)).orderByDesc("timestamp").last("limit " + 2 * count));
            if (records.size() == 0) {
                return Result.success(records);
            }
            ArrayList<ReviewWordVo> normal = getNormal(records, count - priors.size());

            wordVos.addAll(normal);
            Collections.shuffle(wordVos);
            return Result.success(wordVos);
        }

        return Result.fail(500, "查不到记录");
    }

    @Override
    public void setReviewStatus(Long userid) {
        redisTemplate.opsForValue().set(String.valueOf(userid) + ":isReview", "true");
        redisTemplate.expireAt(String.valueOf(userid) + ":isReview", TimeUtils.getNextDayTimestamp());
        redisTemplate.opsForValue().set(String.valueOf(userid) + ":todayReviewNum", "0");
        redisTemplate.expireAt(String.valueOf(userid) + ":todayReviewNum", TimeUtils.getNextDayTimestamp());
    }

    @Transactional
    @Override
    public void updatePriorWord(Long userid, Integer id, Integer dictID, Boolean isRight) {
        String reviewNum = redisTemplate.opsForValue().get(String.valueOf(userid) + ":todayReviewNum");
        if (reviewNum == null) {
            reviewNum = "0";
        }
        redisTemplate.opsForValue().set(String.valueOf(userid) + ":todayReviewNum", String.valueOf(Integer.parseInt(reviewNum) + 1));
        redisTemplate.expireAt(String.valueOf(userid) + ":todayReviewNum", TimeUtils.getNextDayTimestamp());
        if (isRight == false) {
            return;
        }
        log.info(userid + " " + id + " " + dictID);
        ReviewPrior reviewPrior = reviewPriorMapper.selectOne(new QueryWrapper<ReviewPrior>().eq("userid", userid)
                .eq("id", id).eq("dictID", dictID));
        log.info("---------------- " + reviewPrior);
        if (reviewPrior != null && reviewPrior.getReviewCount() == 1) {
            reviewPriorMapper.delete(new QueryWrapper<ReviewPrior>().eq("userid", userid)
                    .eq("id", id).eq("dictID", dictID));

            Record record = new Record(userid, id, reviewPrior.getTimestamp(), dictID);
            recordMapper.insert(record);

            return;
        }

        reviewPriorMapper.updateReviewCnt(userid, id, dictID);
    }
}
