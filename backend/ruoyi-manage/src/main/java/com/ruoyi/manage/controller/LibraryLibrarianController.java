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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage/library/librarian")
public class LibraryLibrarianController extends BaseController {

    @Autowired
    private ILibraryLibrarianService service;

    @PreAuthorize("@ss.hasPermi('manage:libraryLibrarian:list')")
    @GetMapping("/list")
    public TableDataInfo list(LibraryLibrarian query) {
        startPage();
        List<LibraryLibrarian> list = service.selectList(query);
        return getDataTable(list);
    }

    @Log(title = "图书管理员", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('manage:libraryLibrarian:add')")
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> body) {
        Long userId = body == null ? null : (body.get("userId") instanceof Number ? ((Number) body.get("userId")).longValue() : null);
        if (userId == null) return error("userId 必填");
        return toAjax(service.appoint(userId, getUsername()));
    }

    @Log(title = "图书管理员", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('manage:libraryLibrarian:remove')")
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return toAjax(service.dismiss(userIds, getUsername()));
    }
}
