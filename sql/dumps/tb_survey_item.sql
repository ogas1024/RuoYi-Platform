create table tb_survey_item
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    survey_id   bigint                  not null comment '问卷ID（tb_survey.id）',
    title       varchar(300)            not null comment '题目/提示',
    type        tinyint                 not null comment '类型：1文本 2单选 3多选（预留4文件 5时间）',
    required    char        default '0' not null comment '是否必填：0否 1是',
    sort_no     int         default 0   not null comment '排序号（0开始）',
    ext_json    json                    null comment '扩展配置（占位，文件/时间等）',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）'
)
    comment '问卷-题目' charset = utf8mb4;

create index idx_survey_sort
    on tb_survey_item (survey_id, sort_no);

INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (1, 1, '你的自我介绍', 1, '1', 0, null, 'admin', '2025-11-07 21:51:40', '', null, '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (2, 1, '宿舍是否分配完成？', 2, '1', 1, null, 'admin', '2025-11-07 21:51:40', '', null, '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (3, 1, '你感兴趣的社团（可多选）', 3, '0', 2, null, 'admin', '2025-11-07 21:51:41', '', null, '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (4, 2, 'testtest', 1, '0', 0, null, 'admin', '2025-11-07 22:27:56', 'admin', '2025-11-07 22:27:56', '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (5, 2, 'testtest', 1, '1', 1, null, 'admin', '2025-11-07 22:27:56', 'admin', '2025-11-07 22:27:56', '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (6, 2, 'ttt', 2, '0', 2, null, 'admin', '2025-11-07 22:27:56', 'admin', '2025-11-07 22:27:56', '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (7, 2, 'ttt', 3, '0', 3, null, 'admin', '2025-11-07 22:27:56', 'admin', '2025-11-07 22:27:56', '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (8, 3, '', 1, '0', 0, null, 'admin', '2025-11-07 22:33:08', 'admin', '2025-11-07 22:33:08', '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (9, 4, '这是', 1, '0', 0, null, 'admin', '2025-11-07 22:43:10', 'admin', '2025-11-07 22:43:10', '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (10, 5, '测试过', 1, '0', 0, null, 'admin', '2025-11-07 22:43:36', 'admin', '2025-11-07 22:43:36', '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (12, 6, 'ttt', 1, '0', 0, null, 'admin', '2025-11-07 23:54:48', 'admin', '2025-11-07 23:54:48', '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (13, 6, 'a or b', 2, '0', 1, null, 'admin', '2025-11-07 23:54:48', 'admin', '2025-11-07 23:54:48', '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (14, 7, 'aa', 2, '1', 0, null, 'admin', '2025-11-08 09:48:07', 'admin', '2025-11-08 09:48:07', '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (15, 8, 'aaaa', 3, '1', 0, null, 'admin', '2025-11-08 10:44:20', 'admin', '2025-11-08 10:44:20', '0');
INSERT INTO ruoyi.tb_survey_item (id, survey_id, title, type, required, sort_no, ext_json, create_by, create_time, update_by, update_time, del_flag) VALUES (16, 9, 'a', 2, '1', 0, null, 'admin', '2025-11-08 10:49:11', 'admin', '2025-11-08 10:49:11', '0');
