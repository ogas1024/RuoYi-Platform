package com.ruoyi.manage.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.LibraryLibrarian;
import com.ruoyi.manage.service.ILibraryLibrarianService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ruoyi.manage.mapper.SysLinkageMapper;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage/library/librarian")
public class LibraryLibrarianController extends BaseController {

    @Autowired
    private ILibraryLibrarianService service;
    @Autowired
    private SysLinkageMapper sysLinkageMapper;

    /**
     * 图书管理员列表
     * 路径：GET /manage/library/librarian/list
     * 权限：manage:libraryLibrarian:list
     *
     * @param query 查询条件
     * @return 分页数据
     */
    @PreAuthorize("@ss.hasPermi('manage:libraryLibrarian:list')")
    @GetMapping("/list")
    public TableDataInfo list(LibraryLibrarian query) {
        startPage();
        List<LibraryLibrarian> list = service.selectList(query);
        return getDataTable(list);
    }

    /**
     * 任命图书管理员
     * 路径：POST /manage/library/librarian
     * 权限：manage:libraryLibrarian:add
     * 说明：支持通过用户名解析 userId。
     *
     * @param body { userId | username }
     * @return 操作结果
     */
    @Log(title = "图书管理员", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:libraryLibrarian:add')")
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> body) {
        Long userId = null;
        if (body != null) {
            Object uidVal = body.get("userId");
            if (uidVal instanceof Number) userId = ((Number) uidVal).longValue();
            // 兼容按用户名指定
            if (userId == null) {
                Object uname = body.get("username");
                if (uname != null) {
                    String userName = String.valueOf(uname);
                    if (!userName.isEmpty()) {
                        Long resolved = sysLinkageMapper.selectUserIdByUserName(userName);
                        userId = resolved;
                    }
                }
            }
        }
        if (userId == null) return error("请提供有效的用户名");
        return toAjax(service.appoint(userId, getUsername()));
    }

    /**
     * 解除图书管理员
     * 路径：DELETE /manage/library/librarian/{userIds}
     * 权限：manage:libraryLibrarian:remove
     *
     * @param userIds 用户ID数组
     * @return 操作结果
     */
    @Log(title = "图书管理员", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:libraryLibrarian:remove')")
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return toAjax(service.dismiss(userIds, getUsername()));
    }
}
