create table tb_survey_option
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    item_id     bigint                  not null comment '题目ID（tb_survey_item.id）',
    label       varchar(200)            not null comment '选项展示文本',
    value       varchar(200)            null comment '选项值（可空，默认=label）',
    sort_no     int         default 0   not null comment '排序号',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）'
)
    comment '问卷-选项' charset = utf8mb4;

create index idx_item_sort
    on tb_survey_option (item_id, sort_no);

INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (1, 2, '是', null, 0, 'admin', '2025-11-07 21:51:41', '', null, '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (2, 2, '否', null, 1, 'admin', '2025-11-07 21:51:41', '', null, '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (3, 3, '篮球社', null, 0, 'admin', '2025-11-07 21:51:42', '', null, '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (4, 3, '吉他社', null, 1, 'admin', '2025-11-07 21:51:42', '', null, '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (5, 3, '羽毛球社', null, 2, 'admin', '2025-11-07 21:51:42', '', null, '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (6, 7, '333', null, 0, 'admin', '2025-11-07 22:27:56', 'admin', '2025-11-07 22:27:56', '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (7, 7, '333', null, 1, 'admin', '2025-11-07 22:27:56', 'admin', '2025-11-07 22:27:56', '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (8, 13, 'a', null, 0, 'admin', '2025-11-07 23:54:48', 'admin', '2025-11-07 23:54:48', '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (9, 13, 'b', null, 1, 'admin', '2025-11-07 23:54:48', 'admin', '2025-11-07 23:54:48', '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (10, 14, 'a', null, 0, 'admin', '2025-11-08 09:48:07', 'admin', '2025-11-08 09:48:07', '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (11, 14, 'g', null, 1, 'admin', '2025-11-08 09:48:07', 'admin', '2025-11-08 09:48:07', '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (12, 15, 'aaaa', null, 0, 'admin', '2025-11-08 10:44:20', 'admin', '2025-11-08 10:44:20', '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (13, 15, 'dfdf', null, 1, 'admin', '2025-11-08 10:44:20', 'admin', '2025-11-08 10:44:20', '0');
INSERT INTO ruoyi.tb_survey_option (id, item_id, label, value, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (14, 16, 'b', null, 0, 'admin', '2025-11-08 10:49:11', 'admin', '2025-11-08 10:49:11', '0');
