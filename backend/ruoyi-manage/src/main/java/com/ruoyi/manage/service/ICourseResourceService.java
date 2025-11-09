package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.domain.vo.DayCount;
import com.ruoyi.manage.vo.TopUserVO;
import com.ruoyi.manage.domain.vo.PieItem;

import java.util.List;

/**
 * 课程资源 服务接口
 */
public interface ICourseResourceService {
    /**
     * 按主键查询
     */
    CourseResource selectById(Long id);

    /**
     * 列表查询
     * @param query 查询条件
     * @param onlyApprovedForAnonymous 是否仅返回已通过/上架（门户/匿名场景）
     */
    List<CourseResource> selectList(CourseResource query, boolean onlyApprovedForAnonymous);

    /**
     * 新增（进入待审）
     */
    int insert(CourseResource data);

    /**
     * 编辑
     */
    int update(CourseResource data);

    /**
     * 批量删除
     * @param ids ID数组
     * @param currentUserId 当前用户ID
     * @param isAdminOrLead 是否管理员/专业负责人
     */
    int deleteByIds(Long[] ids, Long currentUserId, boolean isAdminOrLead);

    /**
     * 审核通过
     */
    int approve(Long id, String auditor);

    /**
     * 审核驳回
     */
    int reject(Long id, String auditor, String reason);

    /**
     * 下架
     */
    int offline(Long id, String auditor, String reason);

    /**
     * 提交上架（转待审）
     */
    int onlineToPending(Long id);

    /**
     * 下载计数 +1（记录日志）
     */
    int incrDownload(Long id);

    /**
     * TOP 榜
     * @param scope global/major/course
     */
    List<CourseResource> selectTop(String scope, Long majorId, Long courseId, Integer days, Integer limit);

    /**
     * 设为最佳
     */
    int setBest(Long id, String operator);

    /**
     * 取消最佳
     */
    int unsetBest(Long id);

    /**
     * 上传趋势（按日统计 create_time），默认近30天。
     * @param days 天数（含今天），例如 30 表示近30天。
     */
    java.util.List<DayCount> uploadTrend(Integer days);

    /**
     * 下载趋势（按日统计日志 DOWNLOAD），默认近30天。
     * @param days 天数（含今天）
     */
    java.util.List<DayCount> downloadTrend(Integer days);

    /**
     * 用户下载TOP榜
     */
    java.util.List<TopUserVO> topDownloadUsers(Integer limit);

    /**
     * 专业占比（默认近30天）
     */
    java.util.List<PieItem> majorShare(Integer days);

    /**
     * 课程占比（默认近30天）
     */
    java.util.List<PieItem> courseShare(Integer days);

    /**
     * 专业占比（全站，全时段）。
     */
    java.util.List<PieItem> majorShareAll();

    /**
     * 课程占比（全站，全时段）。
     */
    java.util.List<PieItem> courseShareAll();

    /**
     * 判定用户是否为指定专业的负责人（用于控制下载权限等）。
     */
    boolean userHasMajorLeadAccess(Long userId, Long majorId);
}
