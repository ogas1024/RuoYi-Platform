create table sys_dept
(
    dept_id     bigint auto_increment comment '部门id'
        primary key,
    parent_id   bigint      default 0   null comment '父部门id',
    ancestors   varchar(50) default ''  null comment '祖级列表',
    dept_name   varchar(30) default ''  null comment '部门名称',
    order_num   int         default 0   null comment '显示顺序',
    leader      varchar(20)             null comment '负责人',
    phone       varchar(11)             null comment '联系电话',
    email       varchar(50)             null comment '邮箱',
    status      char        default '0' null comment '部门状态（0正常 1停用）',
    del_flag    char        default '0' null comment '删除标志（0代表存在 2代表删除）',
    create_by   varchar(64) default ''  null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  null comment '更新者',
    update_time datetime                null comment '更新时间'
)
    comment '部门表' collate = utf8mb4_general_ci
                     row_format = DYNAMIC;

INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (100, 0, '0', '天佑学院', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-13 15:33:35', 'admin', '2025-10-19 20:03:17');
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (101, 100, '0,100', '2023级', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-13 15:33:35', 'admin', '2025-11-03 15:50:55');
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (102, 100, '0,100', '2022级', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-13 15:33:35', 'admin', '2025-11-03 15:51:02');
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (103, 101, '0,100,101', '2023-1班', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-13 15:33:35', 'admin', '2025-11-03 15:51:14');
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (104, 101, '0,100,101', '市场部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2025-10-13 15:33:35', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (105, 101, '0,100,101', '2023-2班', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-13 15:33:35', 'admin', '2025-11-03 15:51:09');
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (106, 101, '0,100,101', '财务部门', 4, '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2025-10-13 15:33:35', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (107, 101, '0,100,101', '运维部门', 5, '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2025-10-13 15:33:35', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (108, 102, '0,100,102', '2022-1班', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-13 15:33:35', 'admin', '2025-11-03 15:51:19');
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (109, 102, '0,100,102', '2022-2班', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-13 15:33:35', 'admin', '2025-11-03 15:51:23');
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (200, 100, '0,100', '2024级', 0, null, null, null, '0', '0', 'admin', '2025-11-03 15:49:58', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (201, 200, '0,100,200', '2024-1班', 0, null, null, null, '0', '0', 'admin', '2025-11-03 15:50:07', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (202, 200, '0,100,200', '2024-2班', 0, null, null, null, '0', '0', 'admin', '2025-11-03 15:50:15', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (203, 100, '0,100', '2025级', 0, '', null, null, '0', '0', 'admin', '2025-11-03 15:50:29', 'admin', '2025-11-03 15:50:40');
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (204, 203, '0,100,203', '2025-1班', 0, null, null, null, '0', '0', 'admin', '2025-11-03 15:50:48', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (205, 203, '0,100,203', '2025-2班', 0, null, null, null, '0', '0', 'admin', '2025-11-03 15:51:36', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (206, 100, '0,100', '2021级', 0, null, null, null, '0', '0', 'admin', '2025-11-03 15:51:49', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (207, 206, '0,100,206', '2021-1班', 0, null, null, null, '0', '0', 'admin', '2025-11-03 15:52:05', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (208, 206, '0,100,206', '2021-2班', 0, null, null, null, '0', '0', 'admin', '2025-11-03 15:52:12', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (209, 100, '0,100', '2020级', 0, null, null, null, '0', '0', 'admin', '2025-11-03 15:52:28', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (210, 209, '0,100,209', '2020-1班', 0, null, null, null, '0', '0', 'admin', '2025-11-03 15:52:43', '', null);
INSERT INTO ruoyi.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time) VALUES (211, 209, '0,100,209', '2020-2班', 0, null, null, null, '0', '0', 'admin', '2025-11-03 15:52:54', '', null);
