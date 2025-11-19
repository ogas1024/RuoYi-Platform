create table tb_course_resource
(
    id                 bigint auto_increment comment '主键ID'
        primary key,
    major_id           bigint                  not null comment '专业ID（冗余便于索引）',
    course_id          bigint                  not null comment '课程ID',
    resource_name      varchar(255)            not null comment '资源名称（用户自定义）',
    resource_type      tinyint                 not null comment '资源类型：0-文件 1-外链',
    file_url           varchar(512)            null comment '文件直链（OSS，resource_type=0时必填）',
    file_hash          varchar(128)            null comment '文件哈希（SHA-256，resource_type=0时必填）',
    file_size          bigint                  null comment '文件大小（字节）',
    link_url           varchar(512)            null comment '外链URL（resource_type=1时必填）',
    description        text                    not null comment '资源简介（必填）',
    status             tinyint     default 0   not null comment '状态：0待审 1已通过 2驳回 3已下架',
    audit_by           varchar(64)             null comment '审核人',
    audit_time         datetime                null comment '审核时间',
    audit_reason       varchar(512)            null comment '驳回理由',
    publish_time       datetime                null comment '首次上架时间',
    download_count     int         default 0   not null comment '下载次数',
    last_download_time datetime                null comment '最近下载时间',
    uploader_id        bigint                  not null comment '上传者用户ID',
    uploader_name      varchar(64)             not null comment '上传者名称（冗余）',
    create_by          varchar(64) default ''  not null comment '创建者',
    create_time        datetime                null comment '创建时间',
    update_by          varchar(64) default ''  not null comment '更新者',
    update_time        datetime                null comment '更新时间',
    del_flag           char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_course_filehash
        unique (course_id, resource_type, file_hash),
    constraint uk_course_linkurl
        unique (course_id, resource_type, link_url(191))
)
    comment '课程资源表（仅压缩包或外链）' charset = utf8mb4;

create index idx_course_status
    on tb_course_resource (course_id, status);

create index idx_download_count
    on tb_course_resource (download_count);

create index idx_major_course
    on tb_course_resource (major_id, course_id);

create index idx_status_time
    on tb_course_resource (status, create_time);

create index idx_uploader_id
    on tb_course_resource (uploader_id);

INSERT INTO ruoyi.tb_course_resource (id, major_id, course_id, resource_name, resource_type, file_url, file_hash, file_size, link_url, description, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (10003, 4, 4102, 'OStest', 0, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/resource/4/4102/2025/11/04/fa5b58b1d9a9410f9a3964122d68dfe3.zip', 'sha256:64a7167f735968a57e50ebd436cacd1fa0e3135de3461c693d7c3a5b14b1c30c', 27826, null, 'test', 1, 'admin', '2025-11-07 17:22:05', '文件不对', '2025-11-04 17:36:11', 7, '2025-11-05 00:55:19', 1, 'admin', 'admin', '2025-11-04 17:22:59', '', null, '0');
INSERT INTO ruoyi.tb_course_resource (id, major_id, course_id, resource_name, resource_type, file_url, file_hash, file_size, link_url, description, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (10007, 1, 11, '算法作业参考（修订）', 0, '{{fileUrl}}', 'demo-sha256', 1024, null, '补充说明', 3, 'admin', '2025-11-07 17:22:13', '', '2025-11-07 17:17:17', 0, null, 1, 'admin', 'admin', '2025-11-07 17:11:11', 'admin', '2025-11-07 17:19:45', '0');
INSERT INTO ruoyi.tb_course_resource (id, major_id, course_id, resource_name, resource_type, file_url, file_hash, file_size, link_url, description, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (10008, 1, 11, '数据结构复习资料', 0, 'https://.../file.zip', 'sha256-or-etag', 123456, null, '仅供学习参考', 1, 'admin', '2025-11-07 17:16:42', null, '2025-11-07 17:16:41', 0, null, 1, 'admin', 'admin', '2025-11-07 17:15:26', '', null, '0');
INSERT INTO ruoyi.tb_course_resource (id, major_id, course_id, resource_name, resource_type, file_url, file_hash, file_size, link_url, description, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (10010, 4, 4101, 'SDFS', 0, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/resource/4/4101/2025/11/08/bbff680fe59246d2bed19c992555a436.zip', 'sha256:38d89bc2f5763360e010b38a27bf542512bab8b60c1996aef1768d89d6776921', 4480, null, 'sdfa ', 0, null, null, null, null, 0, null, 1, 'admin', 'admin', '2025-11-08 15:22:07', '', null, '0');
INSERT INTO ruoyi.tb_course_resource (id, major_id, course_id, resource_name, resource_type, file_url, file_hash, file_size, link_url, description, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (10011, 4, 4101, 'dddd', 0, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/resource/4/4101/2025/11/08/26b7caa4c5de4b5b8e867a9dddc9dbd1.zip', 'sha256:063aa81fe823d604aae6a8f74f548fac086ec2753d53985c5aabbf23b894fe5d', 72495, null, '啊双方商定、', 1, 'admin', '2025-11-08 16:55:31', null, '2025-11-08 16:55:30', 0, null, 114, 'demo', 'demo', '2025-11-08 16:55:08', '', null, '0');
INSERT INTO ruoyi.tb_course_resource (id, major_id, course_id, resource_name, resource_type, file_url, file_hash, file_size, link_url, description, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (10012, 3, 3001, 'testttt', 0, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/resource/3/3001/2025/11/08/3f7136cf68b84e989d8f602315f1af87.zip', 'sha256:063aa81fe823d604aae6a8f74f548fac086ec2753d53985c5aabbf23b894fe5d', 72495, null, 'aaaa', 1, 'test01', '2025-11-08 22:57:46', 'ttttest', '2025-11-08 22:55:42', 0, null, 115, '2023061001000318', '2023061001000318', '2025-11-08 22:36:34', '', null, '0');
INSERT INTO ruoyi.tb_course_resource (id, major_id, course_id, resource_name, resource_type, file_url, file_hash, file_size, link_url, description, status, audit_by, audit_time, audit_reason, publish_time, download_count, last_download_time, uploader_id, uploader_name, create_by, create_time, update_by, update_time, del_flag) VALUES (10013, 1, 4001, 'tttttsafdf', 0, 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/resource/1/4001/2025/11/09/fdde836a1e704859bf95539c3dbffb1a.zip', 'sha256:063aa81fe823d604aae6a8f74f548fac086ec2753d53985c5aabbf23b894fe5d', 72495, null, 'ojoij', 0, null, null, null, null, 0, null, 114, 'demo', 'demo', '2025-11-09 09:41:16', '', null, '0');
