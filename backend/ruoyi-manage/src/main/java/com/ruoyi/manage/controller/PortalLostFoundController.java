package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.LostItem;
import com.ruoyi.manage.domain.LostItemImage;
import com.ruoyi.manage.service.ILostItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/portal/lostfound")
public class PortalLostFoundController extends BaseController {

    @Autowired
    private ILostItemService service;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public TableDataInfo list(LostItem query) {
        startPage();
        // 门户仅展示已发布；默认隐藏已解决
        if (query == null) query = new LostItem();
        if (query.getSolvedFlag() == null) query.setSolvedFlag(0);
        List<LostItem> list = service.selectList(query, Collections.singletonList(2));
        return getDataTable(list);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public AjaxResult info(@PathVariable Long id) {
        LostItem data = service.selectById(id);
        if (data == null || "2".equals(data.getDelFlag())) return error("条目不存在");
        // 非已发布仅允许作者查看
        if (data.getStatus() == null || data.getStatus() != 2) {
            String username = getUsername();
            if (username == null || !username.equals(data.getCreateBy())) return error("条目不存在或未发布");
        }
        List<LostItemImage> images = service.listImages(id);
        Map<String, Object> res = new HashMap<>();
        res.put("item", data);
        res.put("images", images);
        return success(res);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public AjaxResult add(@RequestBody LostItem body) {
        if (body == null) return error("参数不能为空");
        if (body.getTitle() == null || body.getTitle().length() < 2) return error("标题长度不合法");
        if (body.getContent() == null || body.getContent().length() < 5) return error("正文长度不合法");
        if (body.getContactInfo() != null && body.getContactInfo().length() > 50) return error("联系方式长度超限");
        if (body.getImages() != null && body.getImages().size() > 9) return error("图片最多 9 张");
        body.setCreateBy(getUsername());
        int n = service.insert(body);
        if (n > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", body.getId());
            return success(data);
        }
        return error("保存失败");
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public AjaxResult edit(@PathVariable Long id, @RequestBody LostItem body) {
        if (body == null) return error("参数不能为空");
        LostItem origin = service.selectById(id);
        if (origin == null) return error("条目不存在");
        String username = getUsername();
        if (username == null || !username.equals(origin.getCreateBy())) return error("无权操作");
        if (origin.getSolvedFlag() != null && origin.getSolvedFlag() == 1) return error("已解决的条目不可编辑");
        // 编辑提交后回到待审
        body.setId(id);
        body.setStatus(1);
        body.setUpdateBy(username);
        if (body.getImages() == null) body.setImages(Collections.emptyList());
        return service.update(body) > 0 ? success() : error("更新失败");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        int n = service.softDeleteById(id, getUsername(), false);
        return n > 0 ? success() : error("删除失败或无权删除");
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/solve")
    public AjaxResult solve(@PathVariable Long id) {
        int n = service.solve(id, getUsername());
        if (n > 0) return success();
        return AjaxResult.error(409, "状态冲突：仅已发布且本人可标记已解决");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my/list")
    public TableDataInfo myList(LostItem query) {
        startPage();
        if (query == null) query = new LostItem();
        query.setCreateBy(getUsername());
        List<LostItem> list = service.selectList(query, null);
        return getDataTable(list);
    }
}
