package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.Library;
import com.ruoyi.manage.domain.LibraryAsset;
import com.ruoyi.manage.vo.TopUserVO;

import java.util.List;

public interface ILibraryService {
    Library selectById(Long id);
    List<Library> selectList(Library query, boolean onlyApprovedForPortal);
    int insert(Library data);
    /** 一次性新增图书及其资产，返回新建图书ID */
    Long insertWithAssets(Library data, java.util.List<LibraryAsset> assets);
    int update(Library data);
    int deleteByIds(Long[] ids, Long operatorId, boolean isAdmin);

    int approve(Long id, String operator);
    int reject(Long id, String operator, String reason);
    int offline(Long id, String operator, String reason);
    int onlineToPending(Long id);

    int incrDownload(Long id, Long assetId);
    List<Library> selectTop(Integer limit);
    List<TopUserVO> selectTopUsers(Integer limit);

    List<LibraryAsset> listAssets(Long bookId);
    int addAsset(LibraryAsset asset);
    int deleteAsset(Long assetId);

    boolean isFavorite(Long bookId, Long userId);
    int setFavorite(Long bookId, Long userId, boolean favorite, String operator);

    List<Library> selectFavorites(Long userId);
}
