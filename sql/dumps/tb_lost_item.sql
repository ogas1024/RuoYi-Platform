create table tb_lost_item
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    type           varchar(10)             not null comment '类型：lost=丢失 found=捡到',
    title          varchar(100)            not null comment '标题（2~50 字）',
    content        text                    not null comment '正文描述（5~2000 字）',
    location       varchar(100)            null comment '地点（可选）',
    lost_time      datetime                null comment '发生时间（可选）',
    views          int         default 0   not null comment '浏览量',
    status         char        default '1' not null comment '状态：0草稿 1待审 2已发 3驳回 4下架',
    solved_flag    char        default '0' not null comment '是否已解决：0否 1是（不可回退）',
    publish_time   datetime                null comment '发布时间（通过审核时写入）',
    reject_reason  varchar(500)            null comment '驳回原因（必填于驳回动作）',
    offline_reason varchar(500)            null comment '下架原因（必填于下架动作）',
    contact_info   varchar(50)             null comment '联系信息（自由文本，例如电话/微信等，≤50字）',
    create_by      varchar(64) default ''  not null comment '创建者',
    create_time    datetime                null comment '创建时间',
    update_by      varchar(64) default ''  not null comment '更新者',
    update_time    datetime                null comment '更新时间',
    del_flag       char        default '0' not null comment '删除标志（0存在 2删除）'
)
    comment '失物招领-主体' charset = utf8mb4;

create index idx_creator
    on tb_lost_item (create_by);

create index idx_status_pub
    on tb_lost_item (status, publish_time);

create index idx_title
    on tb_lost_item (title);

create index idx_type_solved
    on tb_lost_item (type, solved_flag);

INSERT INTO ruoyi.tb_lost_item (id, type, title, content, location, lost_time, views, status, solved_flag, publish_time, reject_reason, offline_reason, contact_info, create_by, create_time, update_by, update_time, del_flag) VALUES (1, 'found', '拾到校园卡一张', '操场附近捡到校园卡，联系我取回。电话：188****0001', '操场', '2025-11-05 18:30:00', 0, '2', '1', '2025-11-06 09:00:00', null, '已解决', null, 'admin', null, 'admin', '2025-11-07 10:36:41', '2');
INSERT INTO ruoyi.tb_lost_item (id, type, title, content, location, lost_time, views, status, solved_flag, publish_time, reject_reason, offline_reason, contact_info, create_by, create_time, update_by, update_time, del_flag) VALUES (2, 'lost', '丢失黑色雨伞', '教学楼一层遗失黑色折叠伞，若捡到请联系。微信：lost_user', '教学楼一层', '2025-11-04 08:00:00', 0, '2', '0', '2025-11-06 10:00:00', null, null, null, 'userA', null, 'admin', '2025-11-07 10:37:54', '0');
INSERT INTO ruoyi.tb_lost_item (id, type, title, content, location, lost_time, views, status, solved_flag, publish_time, reject_reason, offline_reason, contact_info, create_by, create_time, update_by, update_time, del_flag) VALUES (3, 'lost', '丢钥匙', '教学楼附近丢失一串钥匙', '教学楼A座', '2025-11-05 18:30:00', 0, '3', '1', '2025-11-07 10:21:25', '信息不完整', '违规内容或已解决归档', '188****0001', 'admin', '2025-11-07 10:18:14', 'admin', '2025-11-07 10:26:04', '2');
INSERT INTO ruoyi.tb_lost_item (id, type, title, content, location, lost_time, views, status, solved_flag, publish_time, reject_reason, offline_reason, contact_info, create_by, create_time, update_by, update_time, del_flag) VALUES (4, 'lost', '丢钥匙-补充描述', '教学楼附近丢失一串钥匙', '教学楼A座', '2025-11-05 18:30:00', 0, '2', '0', '2025-11-07 10:36:03', null, null, 'aegaeg', 'admin', '2025-11-07 10:27:31', 'admin', '2025-11-07 10:37:30', '2');
INSERT INTO ruoyi.tb_lost_item (id, type, title, content, location, lost_time, views, status, solved_flag, publish_time, reject_reason, offline_reason, contact_info, create_by, create_time, update_by, update_time, del_flag) VALUES (5, 'lost', 'test', 'testtesttsettest', 'test', '2025-11-07 10:50:24', 0, '1', '0', null, null, null, 'test', 'admin', '2025-11-07 10:50:35', '', null, '0');
INSERT INTO ruoyi.tb_lost_item (id, type, title, content, location, lost_time, views, status, solved_flag, publish_time, reject_reason, offline_reason, contact_info, create_by, create_time, update_by, update_time, del_flag) VALUES (6, 'found', 'aaaaaaaaa', 'aaaaaaaaaaaaaaaa', '', null, 0, '2', '0', '2025-11-07 11:04:27', null, null, '', 'ogas', '2025-11-07 11:04:01', 'admin', '2025-11-07 14:37:38', '0');
INSERT INTO ruoyi.tb_lost_item (id, type, title, content, location, lost_time, views, status, solved_flag, publish_time, reject_reason, offline_reason, contact_info, create_by, create_time, update_by, update_time, del_flag) VALUES (7, 'found', 'tttt', 'ttttttttttttt', '', null, 0, '1', '0', '2025-11-07 14:54:45', null, null, '', 'ogas', '2025-11-07 14:54:29', 'ogas', '2025-11-07 14:56:13', '0');
