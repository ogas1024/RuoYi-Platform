package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.domain.CrUserScore;

import java.util.List;

/**
 * 积分服务接口
 */
public interface IScoreService {
    /**
     * 审核通过积分奖励
     * @param resource 资源实体
     * @param operator 操作人
     * @return 影响行数
     */
    int awardApprove(CourseResource resource, String operator);

    /**
     * 设置最佳积分奖励
     */
    int awardBest(CourseResource resource, String operator);

    /**
     * 用户积分排行
     * @param majorId 专业ID（可为 null 表示全站）
     * @return 排行列表
     */
    List<CrUserScore> selectRank(Long majorId);

    /**
     * 全站积分TopN（majorId=0），按 total_score 降序。
     */
    List<CrUserScore> selectTopScoreUsers(Integer limit);
}
