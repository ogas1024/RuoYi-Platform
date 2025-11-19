create table tb_major_lead
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    major_id    bigint                  not null comment '专业ID',
    user_id     bigint                  not null comment '负责人用户ID',
    remark      varchar(500)            null comment '备注',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_major_user
        unique (major_id, user_id)
)
    comment '专业负责人映射表' charset = utf8mb4;

create index idx_lead_user
    on tb_major_lead (user_id);

INSERT INTO ruoyi.tb_major_lead (id, major_id, user_id, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (8, 4, 110, '', 'admin', null, '', null, '0');
INSERT INTO ruoyi.tb_major_lead (id, major_id, user_id, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (11, 4, 111, '', 'admin', null, '', null, '0');
INSERT INTO ruoyi.tb_major_lead (id, major_id, user_id, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (12, 100, 111, '', 'admin', null, '', null, '0');
INSERT INTO ruoyi.tb_major_lead (id, major_id, user_id, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (13, 3, 111, '', 'admin', null, '', null, '0');
