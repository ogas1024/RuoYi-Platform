package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.Library;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.vo.DayCount;

import java.util.Date;
import java.util.List;

/**
 * 数字图书馆 Mapper
 */
public interface LibraryMapper {
    Library selectById(Long id);

    /** 列表查询（可含状态/关键字等条件） */
    List<Library> selectList(@Param("query") Library query);

    int insert(Library data);

    int update(Library data);

    /** 软删除（管理员可删除任意，非管理员仅删本人） */
    int softDeleteByIds(@Param("ids") Long[] ids, @Param("userId") Long userId, @Param("admin") boolean admin);

    /** 审核通过 */
    int approve(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") Date auditTime);

    /** 审核驳回 */
    int reject(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") Date auditTime, @Param("reason") String reason);

    /** 下架 */
    int offline(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") java.util.Date auditTime, @Param("reason") String reason);

    /** 提交上架（转待审） */
    int onlineToPending(@Param("id") Long id);

    /** 下载计数+1（记录日志） */
    int incrDownload(@Param("id") Long id, @Param("time") Date time);

    /** 图书下载TopN */
    List<Library> selectTop(@Param("limit") Integer limit);

    /** 用户上传TopN */
    List<com.ruoyi.manage.vo.TopUserVO> selectTopUsers(@Param("limit") Integer limit);

    Integer existsIsbn13(@Param("isbn13") String isbn13);

    Integer existsIsbn13ExcludeId(@Param("isbn13") String isbn13, @Param("id") Long id);

    List<com.ruoyi.manage.domain.Library> selectFavorites(@Param("userId") Long userId);

    /**
     * 统计：按天分组上传数量（create_time）
     */
    List<DayCount> selectUploadCountByDay(@Param("from") Date from, @Param("to") Date to);
}
