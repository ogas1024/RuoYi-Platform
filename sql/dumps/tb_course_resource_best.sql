create table tb_course_resource_best
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    resource_id bigint                  not null comment '资源ID（唯一）',
    best_by     varchar(64)             not null comment '最佳标记人',
    best_time   datetime                not null comment '最佳标记时间',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_best_resource
        unique (resource_id)
)
    comment '课程资源最佳标记表' charset = utf8mb4;

INSERT INTO ruoyi.tb_course_resource_best (id, resource_id, best_by, best_time, create_by, create_time, update_by, update_time, del_flag) VALUES (4, 10005, 'admin', '2025-11-05 09:57:59', 'admin', '2025-11-05 09:57:59', '', null, '0');
INSERT INTO ruoyi.tb_course_resource_best (id, resource_id, best_by, best_time, create_by, create_time, update_by, update_time, del_flag) VALUES (7, 10012, 'test01', '2025-11-08 22:57:58', 'test01', '2025-11-08 22:57:58', '', null, '0');
