package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.Library;
import com.ruoyi.manage.service.ILibraryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.ruoyi.manage.domain.LibraryAsset;
import com.ruoyi.manage.domain.vo.DayCount;

@RestController
@RequestMapping("/manage/library")
public class LibraryController extends BaseController {

    @Autowired
    private ILibraryService service;

    @PreAuthorize("@ss.hasPermi('manage:library:list')")
    @GetMapping("/list")
    public TableDataInfo list(Library query) {
        startPage();
        List<Library> list = service.selectList(query, false);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:library:get')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(service.selectById(id));
    }

    /**
     * 管理端：获取图书资产列表（文件/外链）
     */
    @PreAuthorize("@ss.hasPermi('manage:library:get')")
    @GetMapping("/{id}/assets")
    public AjaxResult listAssets(@PathVariable Long id) {
        List<LibraryAsset> assets = service.listAssets(id);
        return success(assets);
    }

    @Log(title = "数字图书", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:library:add')")
    @PostMapping
    public AjaxResult add(@RequestBody Library data) {
        data.setUploaderId(SecurityUtils.getUserId());
        data.setUploaderName(getUsername());
        data.setCreateBy(getUsername());
        return toAjax(service.insert(data));
    }

    @Log(title = "数字图书", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('manage:library:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody Library data) {
        data.setUpdateBy(getUsername());
        return toAjax(service.update(data));
    }

    @Log(title = "数字图书", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:library:remove')")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        boolean isAdmin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        return toAjax(service.deleteByIds(ids, SecurityUtils.getUserId(), isAdmin));
    }

    @PreAuthorize("@ss.hasPermi('manage:library:approve')")
    @PutMapping("/{id}/approve")
    public AjaxResult approve(@PathVariable Long id) {
        return toAjax(service.approve(id, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('manage:library:reject')")
    @PutMapping("/{id}/reject")
    public AjaxResult reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "") : "";
        return toAjax(service.reject(id, getUsername(), reason));
    }

    @PreAuthorize("@ss.hasPermi('manage:library:offline')")
    @PutMapping("/{id}/offline")
    public AjaxResult offline(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "") : "";
        return toAjax(service.offline(id, getUsername(), reason));
    }

    @PreAuthorize("@ss.hasPermi('manage:library:online')")
    @PutMapping("/{id}/online")
    public AjaxResult online(@PathVariable Long id) {
        return toAjax(service.onlineToPending(id));
    }

    /**
     * 管理端：下载/打开资产
     * 说明：与门户不同，管理端用于审核与回溯，不限制状态；需具备 manage:library:download 权限。
     */
    @PreAuthorize("@ss.hasPermi('manage:library:download')")
    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id, @RequestParam(required = false) Long assetId, HttpServletResponse response) throws IOException {
        Library data = service.selectById(id);
        if (data == null) {
            response.sendError(404, "图书不存在");
            return;
        }
        List<LibraryAsset> assets = service.listAssets(id);
        LibraryAsset target = null;
        if (assetId != null) {
            for (LibraryAsset a : assets) {
                if (a.getId().equals(assetId)) {
                    target = a;
                    break;
                }
            }
        } else {
            String[] order = new String[]{"pdf", "epub", "mobi", "zip"};
            for (String fmt : order) {
                for (LibraryAsset a : assets) {
                    if ("0".equals(a.getAssetType()) && fmt.equalsIgnoreCase(a.getFormat())) {
                        target = a;
                        break;
                    }
                }
                if (target != null) break;
            }
            if (target == null && !assets.isEmpty()) target = assets.get(0);
        }
        if (target == null) {
            response.sendError(404, "无可下载资产");
            return;
        }
        service.incrDownload(id, assetId);
        String url = "0".equals(target.getAssetType()) ? target.getFileUrl() : target.getLinkUrl();
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", url);
    }

    @PreAuthorize("@ss.hasPermi('manage:library:hardRemove')")
    @DeleteMapping("/{ids}/hard")
    public AjaxResult hardRemove(@PathVariable Long[] ids) {
        // 预留：当前仅返回未实现，待补全OSS删除逻辑
        return warn("硬删除暂未实现，请稍后");
    }

    /**
     * 统计：上传趋势（按日计数，近 N 天）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats/uploadTrend")
    public AjaxResult uploadTrend(@RequestParam(required = false, defaultValue = "7") Integer days) {
        java.util.List<DayCount> list = service.uploadTrend(days);
        return success(list);
    }

    /**
     * 统计：下载趋势（按日计数，近 N 天）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats/downloadTrend")
    public AjaxResult downloadTrend(@RequestParam(required = false, defaultValue = "7") Integer days) {
        java.util.List<DayCount> list = service.downloadTrend(days);
        return success(list);
    }

    /**
     * 统计：图书下载榜（按下载次数，默认Top5）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats/topBooks")
    public AjaxResult topBooks(@RequestParam(required = false, defaultValue = "5") Integer limit) {
        return success(service.selectTop(limit));
    }

    /**
     * 统计：用户下载榜（按成功下载次数，默认Top5）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats/topDownloadUsers")
    public AjaxResult topDownloadUsers(@RequestParam(required = false, defaultValue = "5") Integer limit) {
        return success(service.selectTopDownloadUsers(limit));
    }

    /**
     * 统计：用户上传榜（按发布成功数量 TopN）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats/topUploadUsers")
    public AjaxResult topUploadUsers(@RequestParam(required = false, defaultValue = "5") Integer limit) {
        return success(service.selectTopUsers(limit));
    }
}
