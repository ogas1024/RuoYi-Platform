create table tb_survey_answer
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    survey_id    bigint                  not null comment '问卷ID（tb_survey.id）',
    user_id      bigint                  not null comment '提交人用户ID（若依用户）',
    submit_time  datetime                null comment '提交时间',
    update_time2 datetime                null comment '最近修改时间（命名避开审计字段）',
    create_by    varchar(64) default ''  not null comment '创建者',
    create_time  datetime                null comment '创建时间',
    update_by    varchar(64) default ''  not null comment '更新者',
    update_time  datetime                null comment '更新时间',
    del_flag     char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_survey_user
        unique (survey_id, user_id)
)
    comment '问卷-答卷（每人一份）' charset = utf8mb4;

create index idx_survey
    on tb_survey_answer (survey_id);

INSERT INTO ruoyi.tb_survey_answer (id, survey_id, user_id, submit_time, update_time2, create_by, create_time, update_by, update_time, del_flag) VALUES (1, 1, 110, '2025-11-07 22:05:46', '2025-11-07 22:05:46', 'ogas', '2025-11-07 22:05:34', 'ogas', '2025-11-07 22:05:46', '0');
INSERT INTO ruoyi.tb_survey_answer (id, survey_id, user_id, submit_time, update_time2, create_by, create_time, update_by, update_time, del_flag) VALUES (2, 6, 110, '2025-11-08 11:27:05', '2025-11-08 11:27:05', 'ogas', '2025-11-08 09:30:45', 'ogas', '2025-11-08 11:27:05', '0');
INSERT INTO ruoyi.tb_survey_answer (id, survey_id, user_id, submit_time, update_time2, create_by, create_time, update_by, update_time, del_flag) VALUES (3, 7, 110, '2025-11-08 10:11:53', '2025-11-08 10:11:53', 'ogas', '2025-11-08 10:05:58', 'ogas', '2025-11-08 10:11:53', '0');
INSERT INTO ruoyi.tb_survey_answer (id, survey_id, user_id, submit_time, update_time2, create_by, create_time, update_by, update_time, del_flag) VALUES (4, 8, 110, '2025-11-08 11:18:28', '2025-11-08 11:18:28', 'ogas', '2025-11-08 10:44:31', 'ogas', '2025-11-08 11:18:28', '0');
INSERT INTO ruoyi.tb_survey_answer (id, survey_id, user_id, submit_time, update_time2, create_by, create_time, update_by, update_time, del_flag) VALUES (5, 9, 110, '2025-11-08 11:17:15', '2025-11-08 11:17:15', 'ogas', '2025-11-08 11:17:15', 'ogas', '2025-11-08 11:17:15', '0');
INSERT INTO ruoyi.tb_survey_answer (id, survey_id, user_id, submit_time, update_time2, create_by, create_time, update_by, update_time, del_flag) VALUES (6, 6, 113, '2025-11-09 11:20:45', '2025-11-09 11:20:45', 'test03', '2025-11-09 11:20:03', 'test03', '2025-11-09 11:20:45', '0');
