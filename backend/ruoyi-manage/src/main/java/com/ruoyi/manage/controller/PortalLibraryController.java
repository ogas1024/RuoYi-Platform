package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.Library;
import com.ruoyi.manage.domain.LibraryAsset;
import com.ruoyi.manage.service.ILibraryService;
import com.ruoyi.manage.vo.TopUserVO;
import com.ruoyi.manage.vo.LibraryCreateVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/portal/library")
public class PortalLibraryController extends BaseController {

    @Autowired
    private ILibraryService service;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public TableDataInfo list(Library query) {
        startPage();
        boolean onlyApproved = true;
        try {
            // 当查询“我的上传”时（携带 uploaderId 且等于当前用户），放宽为返回全部状态
            if (query != null && query.getUploaderId() != null && getUserId() != null
                    && query.getUploaderId().longValue() == getUserId().longValue()) {
                onlyApproved = false;
            }
        } catch (Exception ignored) {
        }
        List<Library> list = service.selectList(query, onlyApproved);
        return getDataTable(list);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public AjaxResult info(@PathVariable Long id) {
        Library data = service.selectById(id);
        if (data == null) {
            return error("图书不存在");
        }
        // 门户详情：已上架对所有登录用户可见；非上架仅允许上传者本人查看（用于“我的上传”编辑）
        if (data.getStatus() == null || data.getStatus() != 1) {
            Long uid = getUserId();
            if (uid == null || !uid.equals(data.getUploaderId())) {
                return error("图书不存在或未上架");
            }
        }
        List<LibraryAsset> assets = service.listAssets(id);
        boolean favorite = service.isFavorite(id, getUserId());
        Map<String, Object> res = new HashMap<>();
        // 同时返回 library 与 book 两个键，兼容前端不同命名
        res.put("library", data);
        res.put("book", data);
        res.put("assets", assets);
        res.put("favorite", favorite);
        return success(res);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id, @RequestParam(required = false) Long assetId, HttpServletResponse response) throws IOException {
        Library data = service.selectById(id);
        if (data == null || data.getStatus() == null || data.getStatus() != 1) {
            response.sendError(404, "图书不存在或未上架");
            return;
        }
        List<LibraryAsset> assets = service.listAssets(id);
        LibraryAsset target = null;
        if (assetId != null) {
            for (LibraryAsset a : assets)
                if (a.getId().equals(assetId)) {
                    target = a;
                    break;
                }
        } else {
            String[] order = new String[]{"pdf", "epub", "mobi", "zip"};
            for (String fmt : order) {
                for (LibraryAsset a : assets)
                    if ("0".equals(a.getAssetType()) && fmt.equalsIgnoreCase(a.getFormat())) {
                        target = a;
                        break;
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

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public AjaxResult add(@RequestBody Library data) {
        data.setUploaderId(getUserId());
        data.setUploaderName(getUsername());
        data.setCreateBy(getUsername());
        int n = service.insert(data);
        if (n > 0) {
            java.util.Map<String, Object> res = new java.util.HashMap<>();
            res.put("id", data.getId());
            return success(res);
        }
        return error("保存失败");
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public AjaxResult edit(@RequestBody Library data) {
        if (data == null || data.getId() == null) return error("参数不能为空");
        Library origin = service.selectById(data.getId());
        if (origin == null) return error("图书不存在");
        Long uid = getUserId();
        if (uid == null || !uid.equals(origin.getUploaderId())) {
            return error("无权限操作他人图书");
        }
        Integer st = origin.getStatus();
        if (st != null && st == 1) {
            return error("已上架记录不允许编辑");
        }
        data.setUpdateBy(getUsername());
        return toAjax(service.update(data));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(service.deleteByIds(ids, getUserId(), false));
    }

    /**
     * 门户：一次性提交图书信息与资产（文件/外链），进入待审
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/full")
    public AjaxResult addFull(@RequestBody LibraryCreateVO body) {
        if (body == null) return error("参数不能为空");
        body.setUploaderId(getUserId());
        body.setUploaderName(getUsername());
        body.setCreateBy(getUsername());
        Long id = service.insertWithAssets((Library) body, body.getAssets());
        java.util.Map<String, Object> res = new java.util.HashMap<>();
        res.put("id", id);
        return success(res);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/asset")
    public AjaxResult addAsset(@PathVariable Long id, @RequestBody LibraryAsset asset) {
        asset.setBookId(id);
        asset.setCreateBy(getUsername());
        int n = service.addAsset(asset);
        if (n > 0) return success(asset); // 返回包含生成的 asset.id，便于前端删除/展示
        return error("添加失败");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/asset/{assetId}")
    public AjaxResult delAsset(@PathVariable Long id, @PathVariable Long assetId) {
        return toAjax(service.deleteAsset(assetId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/favorite")
    public AjaxResult favorite(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean favorite = body != null && Boolean.TRUE.equals(body.get("favorite"));
        return toAjax(service.setFavorite(id, getUserId(), favorite, getUsername()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/top")
    public AjaxResult top(@RequestParam(required = false, defaultValue = "10") Integer limit) {
        return success(service.selectTop(limit));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/top/users")
    public AjaxResult topUsers(@RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<TopUserVO> list = service.selectTopUsers(limit);
        return success(list);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/favorite")
    public TableDataInfo favoriteList() {
        startPage();
        List<Library> list = service.selectFavorites(getUserId());
        return getDataTable(list);
    }
}
