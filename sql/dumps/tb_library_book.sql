create table tb_library_book
(
    id                 bigint auto_increment comment '主键ID'
        primary key,
    isbn13             char(13)                not null comment 'ISBN-13（仅数字，唯一）',
    title              varchar(256)            not null comment '书名',
    author             varchar(256)            not null comment '作者',
    publisher          varchar(256)            null comment '出版社',
    publish_year       int                     null comment '出版年份',
    language           varchar(32)             null comment '语种',
    keywords           varchar(512)            null comment '关键词（逗号分隔）',
    summary            text                    null comment '简介',
    cover_url          varchar(512)            null comment '封面URL',
    status             char        default '0' not null comment '状态（0待审 1已通过 2驳回 3已下架）',
    audit_by           varchar(64)             null comment '审核人',
    audit_time         datetime                null comment '审核时间',
    audit_reason       varchar(500)            null comment '审核意见/驳回原因',
    publish_time       datetime                null comment '发布时间（通过时写入）',
    download_count     bigint      default 0   not null comment '下载总次数',
    last_download_time datetime                null comment '最近下载时间',
    uploader_id        bigint                  not null comment '上传者用户ID',
    uploader_name      varchar(64) default ''  not null comment '上传者姓名/账号快照',
    create_by          varchar(64) default ''  not null comment '创建者',
    create_time        datetime                null comment '创建时间',
    update_by          varchar(64) default ''  not null comment '更新者',
    update_time        datetime                null comment '更新时间',
    del_flag           char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_isbn13
        unique (isbn13)
)
    comment '数字图书馆-图书主体（v3，library）' charset = utf8mb4;

create index idx_author
    on tb_library_book (author);

create index idx_status_publish
    on tb_library_book (status, publish_time);

create index idx_title
    on tb_library_book (title);

INSERT INTO ruoyi.tb_library_book (id, isbn13, title, author, publisher, publish_year, language, keywords, summary, cover_url, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (1, '9787300000011', '计算机网络（第7版）', '谢希仁', '电子工业出版社', 2017, 'zh', '网络,协议,教材', '经典网络教材', null, '1', 'admin', '2025-11-08 14:13:25', null, '2025-11-08 14:13:25', 9, '2025-11-19 15:38:25', 1001, 'alice', 'alice', '2025-11-05 15:50:17', 'alice', '2025-11-05 15:50:17', '0');
INSERT INTO ruoyi.tb_library_book (id, isbn13, title, author, publisher, publish_year, language, keywords, summary, cover_url, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (2, '9787110000028', 'Java 核心技术 卷I', 'Cay S. Horstmann', '机械工业出版社', 2022, 'zh', 'Java,基础,面向对象', 'Java 入门与进阶', null, '1', 'admin', '2025-11-08 14:13:23', '没有文件', '2025-11-05 20:01:33', 0, null, 1002, 'bob', 'bob', '2025-11-05 15:50:17', 'bob', '2025-11-05 15:50:17', '0');
INSERT INTO ruoyi.tb_library_book (id, isbn13, title, author, publisher, publish_year, language, keywords, summary, cover_url, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (4, '9787110000120', 'Java 核心技术 卷I', 'Cay S. Horstmann', '机械工业出版社', 2022, 'zh', 'Java,基础', '入门到进阶', null, '3', 'admin', '2025-11-08 14:22:50', '没有资源', '2025-11-08 14:13:25', 0, null, 1, 'admin', 'admin', '2025-11-05 17:38:48', '', null, '0');
INSERT INTO ruoyi.tb_library_book (id, isbn13, title, author, publisher, publish_year, language, keywords, summary, cover_url, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (8, '9787121025808', 'test', 'tset', '', null, '', '', '', null, '1', 'admin', '2025-11-08 14:13:21', '', '2025-11-05 23:54:45', 2, '2025-11-19 15:39:08', 1, 'admin', 'admin', '2025-11-05 22:57:03', '', null, '0');
INSERT INTO ruoyi.tb_library_book (id, isbn13, title, author, publisher, publish_year, language, keywords, summary, cover_url, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (9, '9787111359739', '王道', '王道', '', null, '', '', '', null, '1', 'admin', '2025-11-08 14:13:20', '存在无效信息', '2025-11-06 00:10:15', 4, '2025-11-19 15:38:51', 110, 'ogas', 'ogas', '2025-11-06 00:05:02', 'ogas', '2025-11-06 10:13:10', '0');
INSERT INTO ruoyi.tb_library_book (id, isbn13, title, author, publisher, publish_year, language, keywords, summary, cover_url, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (10, '9787111712367', 'testtest', 'test', '', null, '', '', '', null, '1', 'test02', '2025-11-08 15:20:21', null, '2025-11-08 15:20:22', 1, '2025-11-19 15:46:49', 1, 'admin', 'admin', '2025-11-08 15:16:52', '', null, '0');
