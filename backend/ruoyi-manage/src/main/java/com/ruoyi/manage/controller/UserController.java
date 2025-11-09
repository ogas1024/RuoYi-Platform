package com.ruoyi.manage.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.User;
import com.ruoyi.manage.service.IUserService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manage/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 用户列表（卖家管理）
     * 路径：GET /manage/user/list
     * 权限：按业务自定义（如 manage:user:list）
     * 说明：仅查询当前创建者的用户。
     *
     * @param user 查询条件
     * @return 分页数据
     */
    @GetMapping("/list")
    public TableDataInfo list(User user) {
        startPage();
        user.setCreateBy(getUsername());
        List<User> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    /**
     * 新增用户（卖家）
     * 路径：POST /manage/user
     * 权限：按业务自定义（如 manage:user:add）
     * 说明：校验账号唯一，密码加密存储。
     *
     * @param user 用户实体
     * @return 操作结果
     */
    @PostMapping
    public AjaxResult add(@RequestBody User user) {
        if (!userService.checkUserNameUnique(user.getUserName(), user.getUserId())) {
            return error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user).intValue());
    }
}
