create table tb_major
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    major_name  varchar(128)            not null comment '专业名称（唯一）',
    status      char        default '0' not null comment '状态（0正常 1停用）',
    remark      varchar(500)            null comment '备注',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_major_name
        unique (major_name)
)
    comment '专业表（独立）' charset = utf8mb4;

INSERT INTO ruoyi.tb_major (id, major_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (1, '电气工程及其自动化', '0', '培养专业', 'system', '2025-10-18 21:27:36', '', null, '0');
INSERT INTO ruoyi.tb_major (id, major_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (2, '自动化', '0', '培养专业', 'system', '2025-10-18 21:27:36', 'admin', null, '0');
INSERT INTO ruoyi.tb_major (id, major_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (3, '通信工程', '0', '培养专业', 'system', '2025-10-18 21:27:36', '', null, '0');
INSERT INTO ruoyi.tb_major (id, major_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (4, '计算机科学与技术', '0', '培养专业', 'system', '2025-10-18 21:27:36', '', null, '0');
INSERT INTO ruoyi.tb_major (id, major_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (5, '土木工程', '0', '培养专业', 'system', '2025-10-18 21:27:36', '', null, '0');
INSERT INTO ruoyi.tb_major (id, major_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (6, '机械设计制造及其自动化', '0', '培养专业', 'system', '2025-10-18 21:27:36', '', null, '0');
INSERT INTO ruoyi.tb_major (id, major_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (7, '车辆工程', '0', '培养专业', 'system', '2025-10-18 21:27:36', '', null, '0');
INSERT INTO ruoyi.tb_major (id, major_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (8, '交通运输', '0', '培养专业', 'system', '2025-10-18 21:27:36', '', null, '0');
INSERT INTO ruoyi.tb_major (id, major_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (9, '会计学', '0', '培养专业', 'system', '2025-10-18 21:27:36', '', null, '0');
INSERT INTO ruoyi.tb_major (id, major_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (10, '人力资源管理', '0', '培养专业', 'system', '2025-10-18 21:27:36', '', null, '0');
INSERT INTO ruoyi.tb_major (id, major_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (100, '公共', '0', '公共课', 'system', '2025-10-19 00:37:02', '', null, '0');
