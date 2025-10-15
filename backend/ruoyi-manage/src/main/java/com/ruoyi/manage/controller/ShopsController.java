package com.ruoyi.manage.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.User;
import com.ruoyi.manage.service.IUserService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.Shops;
import com.ruoyi.manage.service.IShopsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商铺信息Controller
 * 
 * @author 曾辉
 * @date 2025-10-12
 */
@RestController
@RequestMapping("/manage/shops")
public class ShopsController extends BaseController
{
    @Autowired
    private IShopsService shopsService;

    //新增
    @Autowired
    private IUserService userService;

    /**
     * 查询商铺信息列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Shops shops)
    {
        startPage();
        List<Shops> list = shopsService.selectShopsList(shops);
        return getDataTable(list);
    }

    /**
     * 导出商铺信息列表
     */
    @Log(title = "商铺信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Shops shops)
    {
        List<Shops> list = shopsService.selectShopsList(shops);
        ExcelUtil<Shops> util = new ExcelUtil<Shops>(Shops.class);
        util.exportExcel(response, list, "商铺信息数据");
    }

    /**
     * 获取商铺信息详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(shopsService.selectShopsById(id));
    }

    /**
     * 新增商铺信息
     */
    @Log(title = "商铺信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Shops shops)
    {
        final Long roleId = 101L;  //此处的角色ID为sys_role表中创建角色对应的role_id，请自行修改。
        User user = new User();
        user.setUserName(shops.getUserName());
        user.setPassword(SecurityUtils.encryptPassword(shops.getPassword()));
        user.setNickName(shops.getShopName());
        user.setCreateBy(getUsername());
        user.setRoleId(roleId);
        if (!userService.checkUserNameUnique(user.getUserName(),user.getUserId()))
        {
            return error("新增用户'" + user.getUserName() + "'失败，用户名称已存在");
        }
        Long userId= userService.insertUser(user);
        shops.setId(userId);   //将用户表主键赋给店铺表，以便后面的关联操作
        return toAjax(shopsService.insertShops(shops));
    }

    /**
     * 修改商铺信息
     */
    @Log(title = "商铺信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Shops shops)
    {
        return toAjax(shopsService.updateShops(shops));
    }

    /**
     * 删除商铺信息
     */
    @Log(title = "商铺信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(shopsService.deleteShopsByIds(ids));
    }
}
