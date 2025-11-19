create table tb_notice
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    title            varchar(200)                          not null comment '标题',
    content_html     mediumtext                            not null comment '富文本HTML内容',
    type             tinyint     default 2                 not null comment '类型：1-通知 2-公告',
    status           tinyint     default 0                 not null comment '状态：0-草稿 1-已发布 2-撤回 3-已过期',
    visible_all      tinyint     default 1                 not null comment '是否全员可见：1-是 0-否',
    publisher_id     bigint                                not null comment '发布者用户ID（sys_user.user_id）',
    publish_time     datetime                              null comment '发布时间',
    expire_time      datetime                              null comment '到期时间（超过即视为已过期）',
    pinned           tinyint     default 0                 not null comment '是否置顶：0-否 1-是',
    pinned_time      datetime                              null comment '置顶时间',
    edit_count       int         default 0                 not null comment '编辑次数',
    read_count       int         default 0                 not null comment '阅读次数（冗余计数）',
    attachment_count int         default 0                 not null comment '附件数量（冗余计数）',
    remark           varchar(500)                          null comment '备注',
    create_by        varchar(64) default ''                not null comment '创建者',
    create_time      datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by        varchar(64) default ''                not null comment '更新者',
    update_time      datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag         char        default '0'               not null comment '删除标志（0代表存在 2代表删除）'
)
    comment '通知公告-主体' charset = utf8mb4;

create index idx_notice_expire
    on tb_notice (expire_time);

create index idx_notice_publisher
    on tb_notice (publisher_id);

create index idx_notice_status_pub
    on tb_notice (status, pinned, pinned_time, publish_time);

create index idx_notice_title
    on tb_notice (title);

INSERT INTO ruoyi.tb_notice (id, title, content_html, type, status, visible_all, publisher_id, publish_time, expire_time, pinned, pinned_time, edit_count, read_count, attachment_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (1, '测试公告（全员可见）', '<p>欢迎使用通知公告模块（MVP）</p><p>测试编辑</p>', 2, 1, 1, 1, '2025-10-19 22:13:19', '2025-11-03 13:13:19', 0, '2025-11-03 14:48:23', 1, 4, 0, '示例数据', 'admin', '2025-10-19 22:13:19', 'admin', '2025-11-03 14:51:07', '0');
INSERT INTO ruoyi.tb_notice (id, title, content_html, type, status, visible_all, publisher_id, publish_time, expire_time, pinned, pinned_time, edit_count, read_count, attachment_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (2, '期中考试安排', '<p>本周四发布正式安排</p>', 2, 2, 0, 1, '2025-10-19 22:37:42', '2025-10-18 23:59:59', 1, '2025-10-19 22:40:37', 2, 4, 1, null, 'admin', '2025-10-19 22:35:06', 'admin', '2025-10-19 22:50:39', '2');
INSERT INTO ruoyi.tb_notice (id, title, content_html, type, status, visible_all, publisher_id, publish_time, expire_time, pinned, pinned_time, edit_count, read_count, attachment_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (3, 'test', '<p>test</p>', 2, 2, 1, 1, '2025-11-03 14:48:19', null, 0, null, 0, 1, 0, null, 'admin', '2025-11-03 14:35:06', 'admin', '2025-11-03 15:03:35', '2');
INSERT INTO ruoyi.tb_notice (id, title, content_html, type, status, visible_all, publisher_id, publish_time, expire_time, pinned, pinned_time, edit_count, read_count, attachment_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (4, 'test', '<p>test</p>', 2, 0, 1, 1, null, null, 0, null, 0, 1, 0, null, 'admin', '2025-11-03 14:36:15', 'admin', '2025-11-03 14:48:16', '2');
INSERT INTO ruoyi.tb_notice (id, title, content_html, type, status, visible_all, publisher_id, publish_time, expire_time, pinned, pinned_time, edit_count, read_count, attachment_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (5, 'test', '<p>test</p>', 2, 1, 1, 1, '2025-11-03 14:41:59', null, 0, '2025-11-03 14:42:25', 0, 1, 0, null, 'admin', '2025-11-03 14:37:07', 'admin', '2025-11-03 15:03:37', '2');
INSERT INTO ruoyi.tb_notice (id, title, content_html, type, status, visible_all, publisher_id, publish_time, expire_time, pinned, pinned_time, edit_count, read_count, attachment_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (6, 'test', '<p>test</p>', 2, 1, 1, 1, '2025-11-03 15:03:53', '2025-11-02 00:00:00', 1, '2025-11-03 15:13:32', 1, 1, 0, null, 'admin', '2025-11-03 15:03:52', 'admin', '2025-11-03 15:14:27', '0');
INSERT INTO ruoyi.tb_notice (id, title, content_html, type, status, visible_all, publisher_id, publish_time, expire_time, pinned, pinned_time, edit_count, read_count, attachment_count, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (7, 'testfile', '<p>文件测试</p>', 2, 1, 0, 1, '2025-11-03 15:17:00', null, 0, null, 3, 2, 1, null, 'admin', '2025-11-03 15:16:58', 'admin', '2025-11-07 15:25:44', '0');
