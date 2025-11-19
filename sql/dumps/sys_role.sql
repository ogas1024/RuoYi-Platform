create table sys_role
(
    role_id             bigint auto_increment comment '角色ID'
        primary key,
    role_name           varchar(30)             not null comment '角色名称',
    role_key            varchar(100)            not null comment '角色权限字符串',
    role_sort           int                     not null comment '显示顺序',
    data_scope          char        default '1' null comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    menu_check_strictly tinyint(1)  default 1   null comment '菜单树选择项是否关联显示',
    dept_check_strictly tinyint(1)  default 1   null comment '部门树选择项是否关联显示',
    status              char                    not null comment '角色状态（0正常 1停用）',
    del_flag            char        default '0' null comment '删除标志（0代表存在 2代表删除）',
    create_by           varchar(64) default ''  null comment '创建者',
    create_time         datetime                null comment '创建时间',
    update_by           varchar(64) default ''  null comment '更新者',
    update_time         datetime                null comment '更新时间',
    remark              varchar(500)            null comment '备注'
)
    comment '角色信息表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

INSERT INTO ruoyi.sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark) VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2025-10-13 15:33:36', '', null, '超级管理员');
INSERT INTO ruoyi.sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark) VALUES (2, '普通角色', 'common', 2, '2', 0, 1, '0', '0', 'admin', '2025-10-13 15:33:36', 'admin', '2025-11-08 15:00:44', '普通角色');
INSERT INTO ruoyi.sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark) VALUES (100, '管理员', 'manager', 0, '1', 0, 0, '0', '0', 'admin', '2025-10-13 19:25:22', 'admin', '2025-11-08 11:45:09', null);
INSERT INTO ruoyi.sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark) VALUES (101, '商家', 'seller', 0, '1', 0, 0, '1', '0', 'admin', '2025-10-13 19:25:48', 'admin', '2025-11-03 15:47:22', null);
INSERT INTO ruoyi.sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark) VALUES (102, '买家', 'buyer', 0, '1', 0, 0, '1', '2', 'admin', '2025-10-13 19:26:13', 'admin', '2025-11-03 15:47:24', null);
INSERT INTO ruoyi.sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark) VALUES (103, '专业负责人', 'major_lead', 0, '1', 1, 1, '0', '0', 'admin', '2025-10-18 22:06:31', 'admin', '2025-11-08 11:44:49', null);
INSERT INTO ruoyi.sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark) VALUES (104, '学生', 'student', 0, '1', 1, 1, '0', '2', 'admin', '2025-10-19 11:41:59', 'admin', '2025-11-06 20:26:46', null);
INSERT INTO ruoyi.sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark) VALUES (105, '学生工作者', 'staff', 0, '1', 1, 1, '0', '0', 'admin', '2025-10-20 00:31:00', 'admin', '2025-11-09 00:33:09', null);
INSERT INTO ruoyi.sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark) VALUES (106, '图书管理员', 'librarian', 0, '1', 1, 1, '0', '0', 'admin', '2025-11-05 22:05:56', 'admin', '2025-11-08 13:51:28', null);
