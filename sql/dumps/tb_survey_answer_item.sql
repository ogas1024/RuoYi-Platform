create table tb_survey_answer_item
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    answer_id        bigint                  not null comment '答卷ID（tb_survey_answer.id）',
    item_id          bigint                  not null comment '题目ID（tb_survey_item.id）',
    value_text       text                    null comment '文本题答案',
    value_option_ids json                    null comment '单/多选答案（JSON数组，元素为 tb_survey_option.id）',
    create_by        varchar(64) default ''  not null comment '创建者',
    create_time      datetime                null comment '创建时间',
    update_by        varchar(64) default ''  not null comment '更新者',
    update_time      datetime                null comment '更新时间',
    del_flag         char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_answer_item
        unique (answer_id, item_id)
)
    comment '问卷-答案明细' charset = utf8mb4;

create index idx_answer
    on tb_survey_answer_item (answer_id);

INSERT INTO ruoyi.tb_survey_answer_item (id, answer_id, item_id, value_text, value_option_ids, create_by, create_time, update_by, update_time, del_flag) VALUES (4, 1, 1, 'asdf', null, 'ogas', '2025-11-07 22:05:46', 'ogas', '2025-11-07 22:05:46', '0');
INSERT INTO ruoyi.tb_survey_answer_item (id, answer_id, item_id, value_text, value_option_ids, create_by, create_time, update_by, update_time, del_flag) VALUES (5, 1, 2, null, '[1]', 'ogas', '2025-11-07 22:05:46', 'ogas', '2025-11-07 22:05:46', '0');
INSERT INTO ruoyi.tb_survey_answer_item (id, answer_id, item_id, value_text, value_option_ids, create_by, create_time, update_by, update_time, del_flag) VALUES (6, 1, 3, null, '[]', 'ogas', '2025-11-07 22:05:46', 'ogas', '2025-11-07 22:05:46', '0');
INSERT INTO ruoyi.tb_survey_answer_item (id, answer_id, item_id, value_text, value_option_ids, create_by, create_time, update_by, update_time, del_flag) VALUES (24, 3, 14, null, '[11]', 'ogas', '2025-11-08 10:11:53', 'ogas', '2025-11-08 10:11:53', '0');
INSERT INTO ruoyi.tb_survey_answer_item (id, answer_id, item_id, value_text, value_option_ids, create_by, create_time, update_by, update_time, del_flag) VALUES (25, 5, 16, null, '[14]', 'ogas', '2025-11-08 11:17:15', 'ogas', '2025-11-08 11:17:15', '0');
INSERT INTO ruoyi.tb_survey_answer_item (id, answer_id, item_id, value_text, value_option_ids, create_by, create_time, update_by, update_time, del_flag) VALUES (26, 4, 15, null, '[12]', 'ogas', '2025-11-08 11:18:28', 'ogas', '2025-11-08 11:18:28', '0');
INSERT INTO ruoyi.tb_survey_answer_item (id, answer_id, item_id, value_text, value_option_ids, create_by, create_time, update_by, update_time, del_flag) VALUES (27, 2, 12, null, null, 'ogas', '2025-11-08 11:27:05', 'ogas', '2025-11-08 11:27:05', '0');
INSERT INTO ruoyi.tb_survey_answer_item (id, answer_id, item_id, value_text, value_option_ids, create_by, create_time, update_by, update_time, del_flag) VALUES (28, 2, 13, null, '[8]', 'ogas', '2025-11-08 11:27:05', 'ogas', '2025-11-08 11:27:05', '0');
INSERT INTO ruoyi.tb_survey_answer_item (id, answer_id, item_id, value_text, value_option_ids, create_by, create_time, update_by, update_time, del_flag) VALUES (31, 6, 12, null, null, 'test03', '2025-11-09 11:20:45', 'test03', '2025-11-09 11:20:45', '0');
INSERT INTO ruoyi.tb_survey_answer_item (id, answer_id, item_id, value_text, value_option_ids, create_by, create_time, update_by, update_time, del_flag) VALUES (32, 6, 13, null, '[8]', 'test03', '2025-11-09 11:20:45', 'test03', '2025-11-09 11:20:45', '0');
