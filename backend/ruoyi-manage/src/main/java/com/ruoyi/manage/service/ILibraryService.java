package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.Library;
import com.ruoyi.manage.domain.LibraryAsset;
import com.ruoyi.manage.vo.TopUserVO;
import com.ruoyi.manage.domain.vo.DayCount;

import java.util.List;

/**
 * 数字图书馆 服务接口
 */
public interface ILibraryService {
    /**
     * 按主键查询
     */
    Library selectById(Long id);

    /**
     * 列表查询
     * @param query 查询条件
     * @param onlyApprovedForPortal 门户是否仅返回上架数据
     */
    List<Library> selectList(Library query, boolean onlyApprovedForPortal);

    /**
     * 新增图书
     */
    int insert(Library data);

    /**
     * 一次性新增图书及其资产
     * @param data 图书
     * @param assets 资产列表
     * @return 新建图书ID
     */
    Long insertWithAssets(Library data, java.util.List<LibraryAsset> assets);

    /**
     * 编辑图书
     */
    int update(Library data);

    /**
     * 批量删除（软删）
     * @param ids ID数组
     * @param operatorId 操作人ID
     * @param isAdmin 是否管理员（决定可删除范围）
     */
    int deleteByIds(Long[] ids, Long operatorId, boolean isAdmin);

    /**
     * 审核通过
     */
    int approve(Long id, String operator);

    /**
     * 审核驳回
     */
    int reject(Long id, String operator, String reason);

    /**
     * 下架
     */
    int offline(Long id, String operator, String reason);

    /**
     * 提交上架（转待审）
     */
    int onlineToPending(Long id);

    /**
     * 下载计数 +1（记录日志）
     */
    int incrDownload(Long id, Long assetId);

    /**
     * 图书下载TopN
     */
    List<Library> selectTop(Integer limit);

    /**
     * 用户上传TopN
     */
    List<TopUserVO> selectTopUsers(Integer limit);

    /**
     * 用户下载TopN（成功下载次数）
     */
    List<TopUserVO> selectTopDownloadUsers(Integer limit);

    /**
     * 资产列表
     */
    List<LibraryAsset> listAssets(Long bookId);

    /**
     * 新增资产
     */
    int addAsset(LibraryAsset asset);

    /**
     * 删除资产
     */
    int deleteAsset(Long assetId);

    /**
     * 是否收藏
     */
    boolean isFavorite(Long bookId, Long userId);

    /**
     * 收藏/取消收藏
     */
    int setFavorite(Long bookId, Long userId, boolean favorite, String operator);

    /**
     * 我的收藏列表
     */
    List<Library> selectFavorites(Long userId);

    /**
     * 上传趋势（按日统计 tb_library_book.create_time），默认近30天。
     */
    java.util.List<DayCount> uploadTrend(Integer days);

    /**
     * 下载趋势（按日统计 tb_library_book_download_log.create_time），默认近30天，仅统计成功记录(result='0')。
     */
    java.util.List<DayCount> downloadTrend(Integer days);
}
