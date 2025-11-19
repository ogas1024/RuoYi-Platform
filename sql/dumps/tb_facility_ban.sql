create table tb_facility_ban
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    user_id     bigint                  not null comment '用户ID（申请人）',
    reason      varchar(500)            not null comment '封禁原因',
    status      char        default '0' not null comment '状态（0生效 1失效）',
    expire_time datetime                null comment '到期时间（可选）',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_ban_user_status
        unique (user_id, status)
)
    comment '申请人封禁表' charset = utf8mb4;

create index idx_ban_user
    on tb_facility_ban (user_id);

