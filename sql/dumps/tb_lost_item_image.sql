create table tb_lost_item_image
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    item_id     bigint                  not null comment '条目ID（tb_lost_item.id）',
    url         varchar(500)            not null comment '图片URL（OSS）',
    sort_no     int         default 0   not null comment '排序号（0开始）',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）'
)
    comment '失物招领-图片' charset = utf8mb4;

create index idx_item_sort
    on tb_lost_item_image (item_id, sort_no);

INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (1, 1, 'https://oss-example.com/lostfound/card-1.jpg', 0, '', null, '', null, '0');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (2, 2, 'https://oss-example.com/lostfound/umbrella-1.jpg', 0, '', null, '', '2025-11-07 10:11:25', '2');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (3, 2, 'https://oss-example.com/lostfound/umbrella-1.jpg', 2, 'admin', '2025-11-07 10:11:25', '', null, '0');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (4, 3, 'https://example.com/img1.jpg', 0, 'admin', '2025-11-07 10:18:14', '', null, '0');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (5, 4, 'https://example.com/img1.jpg', 0, 'admin', '2025-11-07 10:27:31', '', '2025-11-07 10:35:30', '2');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (6, 5, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/lostfound/2025/11/07/279ab74397bd454ba811ee01a66db7e7.jpeg', 0, 'admin', '2025-11-07 10:50:35', '', null, '0');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (7, 6, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/lostfound/2025/11/07/5ef4f164ff3142adb30e9a637c52fdd1.jpeg', 0, 'ogas', '2025-11-07 11:04:01', '', '2025-11-07 14:37:37', '2');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (8, 6, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/lostfound/2025/11/07/f7932852554e4d96a930b449ff06ee26.jpeg', 0, 'admin', '2025-11-07 11:09:13', '', '2025-11-07 14:37:37', '2');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (9, 6, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/lostfound/2025/11/07/57d4e4842051494e8a61304268069a9a.jpeg', 0, 'admin', '2025-11-07 14:22:01', '', '2025-11-07 14:37:37', '2');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (10, 6, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/lostfound/2025/11/07/b0d7098f472f4f39a546765d1bccf037.jpeg', 1, 'admin', '2025-11-07 14:22:01', '', '2025-11-07 14:37:37', '2');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (11, 6, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/lostfound/2025/11/07/48d5d8c56097493e9b0257cda663c79e.jpeg', 0, 'ogas', '2025-11-07 14:37:10', '', '2025-11-07 14:37:37', '2');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (12, 7, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/lostfound/2025/11/07/a31eed6c004446d59377316881700855.jpeg', 0, 'ogas', '2025-11-07 14:54:29', '', '2025-11-07 14:56:13', '2');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (13, 7, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/lostfound/2025/11/07/21a2d9da7da343f6b5b990e66c5a4531.jpeg', 0, 'admin', '2025-11-07 14:55:00', '', '2025-11-07 14:56:13', '2');
INSERT INTO ruoyi.tb_lost_item_image (id, item_id, url, sort_no, create_by, create_time, update_by, update_time, del_flag) VALUES (14, 7, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/lostfound/2025/11/07/7755f920e3cf4c1496dc88dec90342da.jpeg', 0, 'admin', '2025-11-07 14:55:21', '', '2025-11-07 14:56:13', '2');
