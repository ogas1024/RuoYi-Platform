package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.CourseResourceLog;
import com.ruoyi.manage.domain.vo.DayCount;
import com.ruoyi.manage.vo.TopUserVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CourseResourceLogMapper {
    int insert(CourseResourceLog log);

    /**
     * 统计：按天分组下载数量（来自日志表，action='DOWNLOAD'）。
     *
     * @param from 起始（含）
     * @param to   截止（不含）
     */
    List<DayCount> selectDownloadCountByDay(@Param("from") Date from, @Param("to") Date to);

    /**
     * 课程资源：用户下载TOP榜（按日志 action='DOWNLOAD' 且 result='SUCCESS' 计数）
     */
    List<TopUserVO> selectTopDownloadUsers(@Param("limit") Integer limit);
}
