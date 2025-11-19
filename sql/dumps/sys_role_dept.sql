create table sys_role_dept
(
    role_id bigint not null comment '角色ID',
    dept_id bigint not null comment '部门ID',
    primary key (role_id, dept_id)
)
    comment '角色和部门关联表' collate = utf8mb4_general_ci
                               row_format = DYNAMIC;

INSERT INTO ruoyi.sys_role_dept (role_id, dept_id) VALUES (2, 100);
INSERT INTO ruoyi.sys_role_dept (role_id, dept_id) VALUES (2, 101);
INSERT INTO ruoyi.sys_role_dept (role_id, dept_id) VALUES (2, 105);
