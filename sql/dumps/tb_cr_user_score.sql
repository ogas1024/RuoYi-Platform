create table tb_cr_user_score
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    user_id       bigint                  not null comment '用户ID（sys_user.id）',
    username      varchar(64) default ''  not null comment '用户名快照（冗余）',
    major_id      bigint      default 0   not null comment '专业ID；0=全站',
    total_score   int         default 0   not null comment '累计积分',
    approve_count int         default 0   not null comment '审核通过次数（仅第一次通过计数）',
    best_count    int         default 0   not null comment '被评为最佳次数（仅第一次计数）',
    remark        varchar(255)            null comment '备注',
    create_by     varchar(64) default ''  not null comment '创建者',
    create_time   datetime                null comment '创建时间',
    update_by     varchar(64) default ''  not null comment '更新者',
    update_time   datetime                null comment '更新时间',
    del_flag      char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_user_major
        unique (user_id, major_id)
)
    comment '课程资源（CR）用户积分聚合表（用户×专业；0=全站）' charset = utf8mb4;

create index idx_major_score
    on tb_cr_user_score (major_id, total_score);

create index idx_user
    on tb_cr_user_score (user_id);

INSERT INTO ruoyi.tb_cr_user_score (id, user_id, username, major_id, total_score, approve_count, best_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (1, 110, 'ogas', 4, 15, 1, 1, null, 'admin', '2025-11-05 09:57:44', 'admin', '2025-11-05 09:57:54', '0');
INSERT INTO ruoyi.tb_cr_user_score (id, user_id, username, major_id, total_score, approve_count, best_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (2, 110, 'ogas', 0, 15, 1, 1, null, 'admin', '2025-11-05 09:57:44', 'admin', '2025-11-05 09:57:54', '0');
INSERT INTO ruoyi.tb_cr_user_score (id, user_id, username, major_id, total_score, approve_count, best_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (5, 1, 'admin', 1, 10, 2, 0, null, 'admin', '2025-11-07 17:16:42', 'admin', '2025-11-07 17:17:18', '0');
INSERT INTO ruoyi.tb_cr_user_score (id, user_id, username, major_id, total_score, approve_count, best_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (6, 1, 'admin', 0, 15, 3, 0, null, 'admin', '2025-11-07 17:16:42', 'admin', '2025-11-07 17:22:05', '0');
INSERT INTO ruoyi.tb_cr_user_score (id, user_id, username, major_id, total_score, approve_count, best_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (9, 1, 'admin', 4, 5, 1, 0, null, 'admin', '2025-11-07 17:22:05', '', null, '0');
INSERT INTO ruoyi.tb_cr_user_score (id, user_id, username, major_id, total_score, approve_count, best_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (11, 114, 'demo', 4, 5, 1, 0, null, 'admin', '2025-11-08 16:55:31', '', null, '0');
INSERT INTO ruoyi.tb_cr_user_score (id, user_id, username, major_id, total_score, approve_count, best_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (12, 114, 'demo', 0, 5, 1, 0, null, 'admin', '2025-11-08 16:55:31', '', null, '0');
INSERT INTO ruoyi.tb_cr_user_score (id, user_id, username, major_id, total_score, approve_count, best_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (13, 115, '2023061001000318', 3, 15, 1, 1, null, 'admin', '2025-11-08 22:55:42', 'admin', '2025-11-08 22:55:55', '0');
INSERT INTO ruoyi.tb_cr_user_score (id, user_id, username, major_id, total_score, approve_count, best_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (14, 115, '2023061001000318', 0, 15, 1, 1, null, 'admin', '2025-11-08 22:55:42', 'admin', '2025-11-08 22:55:55', '0');
