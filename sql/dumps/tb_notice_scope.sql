create table tb_notice_scope
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    notice_id   bigint                                not null comment '公告ID（tb_notice.id）',
    scope_type  tinyint                               not null comment '范围类型：0-角色 1-部门 2-岗位',
    ref_id      bigint                                not null comment '引用ID：sys_role.role_id / sys_dept.dept_id / sys_post.post_id',
    remark      varchar(255)                          null comment '备注',
    create_by   varchar(64) default ''                not null comment '创建者',
    create_time datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   varchar(64) default ''                not null comment '更新者',
    update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag    char        default '0'               not null comment '删除标志（0存在 2删除）',
    constraint uk_scope_unique
        unique (notice_id, scope_type, ref_id, del_flag)
)
    comment '通知公告-可见范围' charset = utf8mb4;

create index idx_scope_notice
    on tb_notice_scope (notice_id);

create index idx_scope_type_ref
    on tb_notice_scope (scope_type, ref_id);

INSERT INTO ruoyi.tb_notice_scope (id, notice_id, scope_type, ref_id, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (7, 2, 0, 2, null, 'admin', '2025-10-19 22:47:04', 'admin', '2025-10-19 22:47:04', '0');
INSERT INTO ruoyi.tb_notice_scope (id, notice_id, scope_type, ref_id, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (8, 2, 1, 10, null, 'admin', '2025-10-19 22:47:04', 'admin', '2025-10-19 22:47:04', '0');
INSERT INTO ruoyi.tb_notice_scope (id, notice_id, scope_type, ref_id, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (9, 2, 2, 5, null, 'admin', '2025-10-19 22:47:04', 'admin', '2025-10-19 22:47:04', '0');
INSERT INTO ruoyi.tb_notice_scope (id, notice_id, scope_type, ref_id, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (12, 7, 1, 103, null, 'admin', '2025-11-07 15:02:16', 'admin', '2025-11-07 15:02:16', '0');
