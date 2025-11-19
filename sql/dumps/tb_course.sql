create table tb_course
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    major_id    bigint                  not null comment '所属专业ID',
    course_name varchar(255)            not null comment '课程名称',
    course_code varchar(64)             null comment '课程编码（可选）',
    status      char        default '0' not null comment '状态（0正常 1停用）',
    remark      varchar(500)            null comment '备注',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_course_unique
        unique (major_id, course_name)
)
    comment '课程表' charset = utf8mb4;

create index idx_course_major
    on tb_course (major_id);

INSERT INTO ruoyi.tb_course (id, major_id, course_name, course_code, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (3001, 3, '信号与系统', 'CE-SS', '0', '', 'system', '2025-10-18 21:27:36', '', null, '0');
INSERT INTO ruoyi.tb_course (id, major_id, course_name, course_code, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (4001, 1, '电路原理', 'EE-CP', '0', '', 'system', '2025-10-18 21:27:36', '', null, '0');
INSERT INTO ruoyi.tb_course (id, major_id, course_name, course_code, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (4101, 4, '数据结构', 'CS-DS', '0', '', 'system', '2025-10-18 21:27:36', '', null, '0');
INSERT INTO ruoyi.tb_course (id, major_id, course_name, course_code, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (4102, 4, '操作系统', 'CS-OS', '0', '', 'system', '2025-10-18 21:27:36', '', null, '0');
