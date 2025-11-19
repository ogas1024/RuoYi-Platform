create table sys_post
(
    post_id     bigint auto_increment comment '岗位ID'
        primary key,
    post_code   varchar(64)            not null comment '岗位编码',
    post_name   varchar(50)            not null comment '岗位名称',
    post_sort   int                    not null comment '显示顺序',
    status      char                   not null comment '状态（0正常 1停用）',
    create_by   varchar(64) default '' null comment '创建者',
    create_time datetime               null comment '创建时间',
    update_by   varchar(64) default '' null comment '更新者',
    update_time datetime               null comment '更新时间',
    remark      varchar(500)           null comment '备注'
)
    comment '岗位信息表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

INSERT INTO ruoyi.sys_post (post_id, post_code, post_name, post_sort, status, create_by, create_time, update_by, update_time, remark) VALUES (1, 'admin', 'Admin', 1, '0', 'admin', '2025-10-13 15:33:36', 'admin', '2025-11-08 13:39:33', '');
INSERT INTO ruoyi.sys_post (post_id, post_code, post_name, post_sort, status, create_by, create_time, update_by, update_time, remark) VALUES (5, 'monitor', '班长', 0, '0', 'admin', '2025-11-07 14:49:40', '', null, null);
