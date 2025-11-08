package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.Library;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.vo.DayCount;

import java.util.Date;
import java.util.List;

public interface LibraryMapper {
    Library selectById(Long id);

    List<Library> selectList(@Param("query") Library query);

    int insert(Library data);

    int update(Library data);

    int softDeleteByIds(@Param("ids") Long[] ids, @Param("userId") Long userId, @Param("admin") boolean admin);

    int approve(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") Date auditTime);

    int reject(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") Date auditTime, @Param("reason") String reason);

    int offline(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("auditTime") java.util.Date auditTime, @Param("reason") String reason);

    int onlineToPending(@Param("id") Long id);

    int incrDownload(@Param("id") Long id, @Param("time") Date time);

    List<Library> selectTop(@Param("limit") Integer limit);

    List<com.ruoyi.manage.vo.TopUserVO> selectTopUsers(@Param("limit") Integer limit);

    Integer existsIsbn13(@Param("isbn13") String isbn13);

    Integer existsIsbn13ExcludeId(@Param("isbn13") String isbn13, @Param("id") Long id);

    List<com.ruoyi.manage.domain.Library> selectFavorites(@Param("userId") Long userId);

    /**
     * 统计：按天分组上传数量（create_time）
     */
    List<DayCount> selectUploadCountByDay(@Param("from") Date from, @Param("to") Date to);
}
