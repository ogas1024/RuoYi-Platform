create table tb_cr_user_score_log
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    user_id     bigint                  not null comment '得分用户ID（资源上传者）',
    username    varchar(64) default ''  not null comment '用户名快照（冗余）',
    major_id    bigint      default 0   not null comment '资源所属专业ID；0=全站',
    resource_id bigint                  not null comment '资源ID（tb_course_resource.id）',
    event_type  varchar(16)             not null comment 'APPROVE/BEST',
    delta       int                     not null comment '积分变动（正数）',
    remark      varchar(255)            null comment '备注',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_once_event
        unique (user_id, resource_id, event_type)
)
    comment '课程资源（CR）用户积分变动流水（一次性发放）' charset = utf8mb4;

create index idx_major_time
    on tb_cr_user_score_log (major_id, create_time);

create index idx_user_time
    on tb_cr_user_score_log (user_id, create_time);

INSERT INTO ruoyi.tb_cr_user_score_log (id, user_id, username, major_id, resource_id, event_type, delta, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (1, 110, 'ogas', 4, 10005, 'APPROVE', 5, null, 'admin', '2025-11-05 09:57:44', '', null, '0');
INSERT INTO ruoyi.tb_cr_user_score_log (id, user_id, username, major_id, resource_id, event_type, delta, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (2, 110, 'ogas', 4, 10005, 'BEST', 10, null, 'admin', '2025-11-05 09:57:54', '', null, '0');
INSERT INTO ruoyi.tb_cr_user_score_log (id, user_id, username, major_id, resource_id, event_type, delta, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (7, 1, 'admin', 1, 10008, 'APPROVE', 5, null, 'admin', '2025-11-07 17:16:42', '', null, '0');
INSERT INTO ruoyi.tb_cr_user_score_log (id, user_id, username, major_id, resource_id, event_type, delta, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (8, 1, 'admin', 1, 10007, 'APPROVE', 5, null, 'admin', '2025-11-07 17:17:18', '', null, '0');
INSERT INTO ruoyi.tb_cr_user_score_log (id, user_id, username, major_id, resource_id, event_type, delta, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (12, 1, 'admin', 4, 10003, 'APPROVE', 5, null, 'admin', '2025-11-07 17:22:05', '', null, '0');
INSERT INTO ruoyi.tb_cr_user_score_log (id, user_id, username, major_id, resource_id, event_type, delta, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (13, 114, 'demo', 4, 10011, 'APPROVE', 5, null, 'admin', '2025-11-08 16:55:31', '', null, '0');
INSERT INTO ruoyi.tb_cr_user_score_log (id, user_id, username, major_id, resource_id, event_type, delta, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (14, 115, '2023061001000318', 3, 10012, 'APPROVE', 5, null, 'admin', '2025-11-08 22:55:42', '', null, '0');
INSERT INTO ruoyi.tb_cr_user_score_log (id, user_id, username, major_id, resource_id, event_type, delta, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (15, 115, '2023061001000318', 3, 10012, 'BEST', 10, null, 'admin', '2025-11-08 22:55:55', '', null, '0');
