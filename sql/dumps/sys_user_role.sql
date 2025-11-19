create table sys_user_role
(
    user_id bigint not null comment '用户ID',
    role_id bigint not null comment '角色ID',
    primary key (user_id, role_id)
)
    comment '用户和角色关联表' collate = utf8mb4_general_ci
                               row_format = DYNAMIC;

INSERT INTO ruoyi.sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO ruoyi.sys_user_role (user_id, role_id) VALUES (107, 101);
INSERT INTO ruoyi.sys_user_role (user_id, role_id) VALUES (108, 101);
INSERT INTO ruoyi.sys_user_role (user_id, role_id) VALUES (110, 100);
INSERT INTO ruoyi.sys_user_role (user_id, role_id) VALUES (111, 103);
INSERT INTO ruoyi.sys_user_role (user_id, role_id) VALUES (112, 106);
INSERT INTO ruoyi.sys_user_role (user_id, role_id) VALUES (113, 105);
INSERT INTO ruoyi.sys_user_role (user_id, role_id) VALUES (116, 105);
INSERT INTO ruoyi.sys_user_role (user_id, role_id) VALUES (117, 106);
