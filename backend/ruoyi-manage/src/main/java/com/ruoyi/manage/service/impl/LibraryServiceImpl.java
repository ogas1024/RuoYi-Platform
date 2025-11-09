package com.ruoyi.manage.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.manage.domain.Library;
import com.ruoyi.manage.domain.LibraryAsset;
import com.ruoyi.manage.domain.LibraryDownloadLog;
import com.ruoyi.manage.domain.LibraryFavorite;
import com.ruoyi.manage.mapper.*;
import com.ruoyi.manage.service.ILibraryService;
import com.ruoyi.manage.vo.TopUserVO;
import com.ruoyi.manage.domain.vo.DayCount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Pattern;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;

/**
 * 数字图书馆服务实现
 * 职责：图书及资产的增删改查、上下架与审核、收藏与下载日志、Top榜与趋势统计。
 * 规则：
 * - ISBN13 统一校验与标准化；
 * - 门户默认仅返回上架数据；
 * - 写操作涉及多表的采用事务保证一致性。
 */
@Service
public class LibraryServiceImpl implements ILibraryService {

    @Autowired
    private LibraryMapper libraryMapper;
    @Autowired
    private LibraryAssetMapper assetMapper;
    @Autowired
    private LibraryFavoriteMapper favoriteMapper;
    @Autowired
    private LibraryDownloadLogMapper downloadLogMapper;

    @Override
    public Library selectById(Long id) {
        return libraryMapper.selectById(id);
    }

    @Override
    public List<Library> selectList(Library query, boolean onlyApprovedForPortal) {
        if (onlyApprovedForPortal) {
            if (query == null) query = new Library();
            query.setStatus(1);
        }
        return libraryMapper.selectList(query);
    }

    @Override
    @Transactional
    public int insert(Library data) {
        String normIsbn = normalizeIsbn13(data.getIsbn13());
        if (!isValidIsbn13(normIsbn)) {
            throw new ServiceException("isbn13 无效（需13位且校验位正确）");
        }
        data.setIsbn13(normIsbn);
        Integer dup = libraryMapper.existsIsbn13(normIsbn);
        if (dup != null && dup > 0) {
            throw new ServiceException("ISBN 已存在");
        }
        data.setStatus(0);
        data.setCreateTime(DateUtils.getNowDate());
        return libraryMapper.insert(data);
    }

    @Override
    @Transactional
    public Long insertWithAssets(Library data, java.util.List<LibraryAsset> assets) {
        int n = insert(data);
        if (n > 0 && assets != null) {
            int sort = 0;
            for (LibraryAsset a : assets) {
                if (a == null) continue;
                a.setBookId(data.getId());
                // 默认值防御：资产类型/排序/格式
                if (a.getAssetType() == null || a.getAssetType().isEmpty()) {
                    if (a.getLinkUrl() != null && !a.getLinkUrl().isEmpty()) a.setAssetType("1");
                    else a.setAssetType("0");
                }
                if (a.getSort() == null) {
                    a.setSort(sort++);
                }
                if ((a.getFormat() == null || a.getFormat().isEmpty()) && a.getFileUrl() != null) {
                    a.setFormat(guessFormatFromUrl(a.getFileUrl()));
                }
                if (a.getCreateBy() == null) a.setCreateBy(data.getCreateBy());
                a.setCreateTime(DateUtils.getNowDate());
                assetMapper.insert(a);
            }
        }
        return data.getId();
    }

    @Override
    @Transactional
    public int update(Library data) {
        if (data.getIsbn13() != null) {
            String normIsbn = normalizeIsbn13(data.getIsbn13());
            if (!isValidIsbn13(normIsbn)) {
                throw new ServiceException("isbn13 无效（需13位且校验位正确）");
            }
            Integer dup = libraryMapper.existsIsbn13ExcludeId(normIsbn, data.getId());
            if (dup != null && dup > 0) {
                throw new ServiceException("ISBN 已存在");
            }
            data.setIsbn13(normIsbn);
        }
        data.setStatus(0);
        data.setUpdateTime(DateUtils.getNowDate());
        return libraryMapper.update(data);
    }

    @Override
    @Transactional
    public int deleteByIds(Long[] ids, Long operatorId, boolean isAdmin) {
        return libraryMapper.softDeleteByIds(ids, operatorId, isAdmin);
    }

    @Override
    @Transactional
    public int approve(Long id, String operator) {
        return libraryMapper.approve(id, operator, DateUtils.getNowDate());
    }

    @Override
    @Transactional
    public int reject(Long id, String operator, String reason) {
        return libraryMapper.reject(id, operator, DateUtils.getNowDate(), reason);
    }

    @Override
    @Transactional
    public int offline(Long id, String operator, String reason) {
        return libraryMapper.offline(id, operator, DateUtils.getNowDate(), reason);
    }

    @Override
    @Transactional
    public int onlineToPending(Long id) {
        return libraryMapper.onlineToPending(id);
    }

    @Override
    @Transactional
    public int incrDownload(Long id, Long assetId) {
        int rows = libraryMapper.incrDownload(id, DateUtils.getNowDate());
        if (rows > 0) {
            LibraryDownloadLog log = new LibraryDownloadLog();
            log.setBookId(id);
            log.setAssetId(assetId);
            // 记录下载的用户与基础请求信息（未登录将抛出401）
            Long userId = SecurityUtils.getUserId();
            log.setUserId(userId);
            HttpServletRequest request = ServletUtils.getRequest();
            if (request != null) {
                log.setIp(IpUtils.getIpAddr());
                String ua = request.getHeader("User-Agent");
                if (ua != null && ua.length() > 256) ua = ua.substring(0, 256);
                log.setUa(ua);
                String ref = request.getHeader("Referer");
                if (ref != null && ref.length() > 256) ref = ref.substring(0, 256);
                log.setReferer(ref);
            }
            log.setResult("0");
            log.setCreateTime(DateUtils.getNowDate());
            downloadLogMapper.insert(log);
        }
        return rows;
    }

    @Override
    public List<Library> selectTop(Integer limit) {
        return libraryMapper.selectTop(limit);
    }

    @Override
    public List<TopUserVO> selectTopUsers(Integer limit) {
        return libraryMapper.selectTopUsers(limit);
    }

    @Override
    public java.util.List<TopUserVO> selectTopDownloadUsers(Integer limit) {
        return downloadLogMapper.selectTopDownloadUsers(limit);
    }

    @Override
    public List<LibraryAsset> listAssets(Long bookId) {
        return assetMapper.selectByBookId(bookId);
    }

    @Override
    @Transactional
    public int addAsset(LibraryAsset asset) {
        // 仅允许上传者本人在“待审/驳回/下架”状态下增添资产
        if (asset == null || asset.getBookId() == null) {
            throw new ServiceException("参数不完整");
        }
        Library book = libraryMapper.selectById(asset.getBookId());
        if (book == null) {
            throw new ServiceException("图书不存在");
        }
        Long uid = SecurityUtils.getUserId();
        if (uid == null || !uid.equals(book.getUploaderId())) {
            throw new ServiceException("无权限操作他人图书");
        }
        Integer st = book.getStatus();
        if (st != null && st == 1) {
            throw new ServiceException("已上架记录不允许变更资产");
        }
        if (asset.getAssetType() == null || asset.getAssetType().isEmpty()) {
            if (asset.getLinkUrl() != null && !asset.getLinkUrl().isEmpty()) asset.setAssetType("1");
            else asset.setAssetType("0");
        }
        if (asset.getSort() == null) asset.setSort(0);
        if ((asset.getFormat() == null || asset.getFormat().isEmpty()) && asset.getFileUrl() != null) {
            asset.setFormat(guessFormatFromUrl(asset.getFileUrl()));
        }
        asset.setCreateTime(DateUtils.getNowDate());
        return assetMapper.insert(asset);
    }

    @Override
    @Transactional
    public int deleteAsset(Long assetId) {
        if (assetId == null) return 0;
        LibraryAsset asset = assetMapper.selectById(assetId);
        if (asset == null) return 0;
        Library book = libraryMapper.selectById(asset.getBookId());
        if (book == null) return 0;
        Long uid = SecurityUtils.getUserId();
        if (uid == null || !uid.equals(book.getUploaderId())) {
            throw new ServiceException("无权限操作他人图书");
        }
        Integer st = book.getStatus();
        if (st != null && st == 1) {
            throw new ServiceException("已上架记录不允许变更资产");
        }
        return assetMapper.deleteById(assetId);
    }

    @Override
    public boolean isFavorite(Long bookId, Long userId) {
        Integer c = favoriteMapper.exists(bookId, userId);
        return c != null && c > 0;
    }

    @Override
    @Transactional
    public int setFavorite(Long bookId, Long userId, boolean favorite, String operator) {
        if (favorite) {
            if (isFavorite(bookId, userId)) return 1;
            LibraryFavorite fav = new LibraryFavorite();
            fav.setBookId(bookId);
            fav.setUserId(userId);
            fav.setCreateBy(operator);
            fav.setCreateTime(DateUtils.getNowDate());
            return favoriteMapper.insert(fav);
        } else {
            return favoriteMapper.delete(bookId, userId);
        }
    }

    @Override
    public List<Library> selectFavorites(Long userId) {
        return libraryMapper.selectFavorites(userId);
    }

    @Override
    public java.util.List<DayCount> uploadTrend(Integer days) {
        int d = (days == null || days <= 0 || days > 365) ? 7 : days;
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate fromDate = today.minusDays(d - 1L);
        java.time.LocalDate toDate = today.plusDays(1L);
        java.util.Date from = java.sql.Timestamp.valueOf(fromDate.atStartOfDay());
        java.util.Date to = java.sql.Timestamp.valueOf(toDate.atStartOfDay());

        java.util.List<DayCount> raw = libraryMapper.selectUploadCountByDay(from, to);
        java.util.HashMap<String, Long> map = new java.util.HashMap<>();
        if (raw != null) {
            for (DayCount dc : raw) {
                if (dc != null && dc.getDay() != null) {
                    map.put(dc.getDay(), dc.getCount() == null ? 0L : dc.getCount());
                }
            }
        }
        java.util.ArrayList<DayCount> result = new java.util.ArrayList<>();
        for (java.time.LocalDate cursor = fromDate; cursor.isBefore(toDate); cursor = cursor.plusDays(1)) {
            String key = cursor.toString();
            DayCount dc = new DayCount();
            dc.setDay(key);
            dc.setCount(map.getOrDefault(key, 0L));
            result.add(dc);
        }
        return result;
    }

    @Override
    public java.util.List<DayCount> downloadTrend(Integer days) {
        int d = (days == null || days <= 0 || days > 365) ? 7 : days;
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate fromDate = today.minusDays(d - 1L);
        java.time.LocalDate toDate = today.plusDays(1L);
        java.util.Date from = java.sql.Timestamp.valueOf(fromDate.atStartOfDay());
        java.util.Date to = java.sql.Timestamp.valueOf(toDate.atStartOfDay());

        java.util.List<DayCount> raw = downloadLogMapper.selectDownloadCountByDay(from, to);
        java.util.HashMap<String, Long> map = new java.util.HashMap<>();
        if (raw != null) {
            for (DayCount dc : raw) {
                if (dc != null && dc.getDay() != null) {
                    map.put(dc.getDay(), dc.getCount() == null ? 0L : dc.getCount());
                }
            }
        }
        java.util.ArrayList<DayCount> result = new java.util.ArrayList<>();
        for (java.time.LocalDate cursor = fromDate; cursor.isBefore(toDate); cursor = cursor.plusDays(1)) {
            String key = cursor.toString();
            DayCount dc = new DayCount();
            dc.setDay(key);
            dc.setCount(map.getOrDefault(key, 0L));
            result.add(dc);
        }
        return result;
    }

    // 只保留数字，去除连字符/空白
    private String normalizeIsbn13(String raw) {
        if (raw == null) return null;
        return raw.replaceAll("[^0-9]", "");
    }

    // 校验13位并验证ISBN-13校验位
    private boolean isValidIsbn13(String s) {
        if (s == null || s.length() != 13 || !Pattern.matches("\\d{13}", s)) return false;
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = s.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : d * 3;
        }
        int check = (10 - (sum % 10)) % 10;
        return check == (s.charAt(12) - '0');
    }

    private String guessFormatFromUrl(String url) {
        if (url == null) return null;
        String u = url;
        int q = u.indexOf('?');
        if (q > -1) u = u.substring(0, q);
        int h = u.indexOf('#');
        if (h > -1) u = u.substring(0, h);
        int p = u.lastIndexOf('.');
        if (p > -1 && p < u.length() - 1) {
            String ext = u.substring(p + 1).toLowerCase();
            // 常见的 .tar.gz 等多重扩展名
            if (ext.equals("gz") && u.endsWith(".tar.gz")) return "tar.gz";
            if (ext.equals("bz2") && u.endsWith(".tar.bz2")) return "tar.bz2";
            if (ext.equals("xz") && u.endsWith(".tar.xz")) return "tar.xz";
            return ext;
        }
        return null;
    }
}
