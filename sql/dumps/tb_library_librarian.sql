create table tb_library_librarian
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    user_id     bigint                  not null comment '系统用户ID',
    username    varchar(64)             null comment '账号快照',
    nickname    varchar(64)             null comment '姓名快照',
    remark      varchar(255)            null comment '备注',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '任命时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_user
        unique (user_id)
)
    comment '数字图书馆-图书管理员映射（与系统角色联动）' charset = utf8mb4;

INSERT INTO ruoyi.tb_library_librarian (id, user_id, username, nickname, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (7, 112, 'test02', '图书管理员演示', null, 'admin', '2025-11-08 15:17:48', '', null, '0');
