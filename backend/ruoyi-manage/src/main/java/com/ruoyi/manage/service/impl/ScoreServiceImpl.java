package com.ruoyi.manage.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.domain.CrUserScore;
import com.ruoyi.manage.mapper.CrUserScoreLogMapper;
import com.ruoyi.manage.mapper.CrUserScoreMapper;
import com.ruoyi.manage.service.IScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;
import java.util.List;

@Service
public class ScoreServiceImpl implements IScoreService {

    private static final int SCORE_APPROVE = 5;
    private static final int SCORE_BEST = 10;

    @Autowired
    private CrUserScoreLogMapper logMapper;
    @Autowired
    private CrUserScoreMapper scoreMapper;

    @Override
    @Transactional
    public int awardApprove(CourseResource r, String operator) {
        if (r == null || r.getUploaderId() == null) return 0;
        Date now = DateUtils.getNowDate();
        int inserted = logMapper.insertApproveLogIfAbsent(r.getUploaderId(), r.getUploaderName(), r.getMajorId(), r.getId(), SCORE_APPROVE, operator, now);
        if (inserted > 0) {
            // 专业维度与全站维度各计一份
            scoreMapper.upsertScore(r.getUploaderId(), r.getUploaderName(), r.getMajorId(), SCORE_APPROVE, 1, 0, operator, now);
            scoreMapper.upsertScore(r.getUploaderId(), r.getUploaderName(), 0L, SCORE_APPROVE, 1, 0, operator, now);
        }
        return inserted > 0 ? SCORE_APPROVE : 0;
    }

    @Override
    @Transactional
    public int awardBest(CourseResource r, String operator) {
        if (r == null || r.getUploaderId() == null) return 0;
        Date now = DateUtils.getNowDate();
        int inserted = logMapper.insertBestLogIfAbsent(r.getUploaderId(), r.getUploaderName(), r.getMajorId(), r.getId(), SCORE_BEST, operator, now);
        if (inserted > 0) {
            scoreMapper.upsertScore(r.getUploaderId(), r.getUploaderName(), r.getMajorId(), SCORE_BEST, 0, 1, operator, now);
            scoreMapper.upsertScore(r.getUploaderId(), r.getUploaderName(), 0L, SCORE_BEST, 0, 1, operator, now);
        }
        return inserted > 0 ? SCORE_BEST : 0;
    }

    @Override
    public List<CrUserScore> selectRank(Long majorId) {
        return scoreMapper.selectRank(majorId);
    }

    @Override
    public java.util.List<CrUserScore> selectTopScoreUsers(Integer limit) {
        int n = (limit == null || limit <= 0 || limit > 100) ? 5 : limit;
        java.util.List<CrUserScore> all = scoreMapper.selectRank(0L);
        if (all == null) return java.util.Collections.emptyList();
        return all.size() > n ? all.subList(0, n) : all;
    }
}
