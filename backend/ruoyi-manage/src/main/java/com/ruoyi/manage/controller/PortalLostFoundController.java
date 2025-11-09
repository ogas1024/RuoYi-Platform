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

    /**
     * 门户列表查询
     * 路径：GET /portal/lostfound/list
     * 权限：已登录（isAuthenticated）
     * 说明：仅展示“已发布(2)”的记录；默认隐藏已解决（solvedFlag=0）。支持分页通用参数。
     *
     * @param query 查询条件（标题、类型、时间范围、是否已解决等）
     * @return 分页数据表格
     */
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

    /**
     * 门户详情
     * 路径：GET /portal/lostfound/{id}
     * 权限：已登录（isAuthenticated）
     * 说明：
     *  - 仅“已发布”对所有登录用户可见；非已发布仅作者本人可看。
     *  - 返回主体与图片列表。
     *
     * @param id 记录ID
     * @return { item, images }
     */
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

    /**
     * 新增（进入待审）
     * 路径：POST /portal/lostfound
     * 权限：已登录（isAuthenticated）
     * 说明：校验标题/正文/联系方式长度与图片数量（≤9），作者为当前登录用户。
     *
     * @param body 请求体（含图片数组）
     * @return 成功返回 {id}
     */
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

    /**
     * 编辑（作者本人）
     * 路径：PUT /portal/lostfound/{id}
     * 权限：已登录（isAuthenticated）
     * 说明：仅作者且未标记已解决可编辑；编辑后回到“待审(1)”。
     *
     * @param id   记录ID
     * @param body 请求体（可包含图片列表）
     * @return 操作结果
     */
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

    /**
     * 删除（软删）
     * 路径：DELETE /portal/lostfound/{id}
     * 权限：已登录（isAuthenticated）
     * 说明：作者本人可删除；实际为软删除。
     *
     * @param id 记录ID
     * @return 操作结果
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        int n = service.softDeleteById(id, getUsername(), false);
        return n > 0 ? success() : error("删除失败或无权删除");
    }

    /**
     * 标记已解决（作者本人）
     * 路径：PUT /portal/lostfound/{id}/solve
     * 权限：已登录（isAuthenticated）
     * 说明：仅“已发布”记录且作者本人可操作。
     *
     * @param id 记录ID
     * @return 操作结果，冲突返回409
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/solve")
    public AjaxResult solve(@PathVariable Long id) {
        int n = service.solve(id, getUsername());
        if (n > 0) return success();
        return AjaxResult.error(409, "状态冲突：仅已发布且本人可标记已解决");
    }

    /**
     * 我的列表
     * 路径：GET /portal/lostfound/my/list
     * 权限：已登录（isAuthenticated）
     * 说明：仅返回当前登录者创建的所有状态记录。
     *
     * @param query 查询条件
     * @return 分页数据
     */
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
