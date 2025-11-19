create table tb_notice_attachment
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    notice_id   bigint                                not null comment '公告ID（tb_notice.id）',
    file_name   varchar(255)                          not null comment '文件名',
    file_url    varchar(1024)                         not null comment '文件URL（OSS）',
    file_type   varchar(50)                           null comment '文件类型（扩展名或MIME）',
    file_size   bigint                                null comment '文件大小（字节）',
    sort        int         default 0                 not null comment '排序',
    remark      varchar(255)                          null comment '备注',
    create_by   varchar(64) default ''                not null comment '创建者',
    create_time datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   varchar(64) default ''                not null comment '更新者',
    update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag    char        default '0'               not null comment '删除标志（0存在 2删除）'
)
    comment '通知公告-附件' charset = utf8mb4;

create index idx_attach_notice
    on tb_notice_attachment (notice_id);

INSERT INTO ruoyi.tb_notice_attachment (id, notice_id, file_name, file_url, file_type, file_size, sort, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (3, 2, '安排.pdf', 'https://oss/bucket/arrange.pdf', 'pdf', 102400, 1, null, 'admin', '2025-10-19 22:47:03', 'admin', '2025-10-19 22:47:03', '0');
INSERT INTO ruoyi.tb_notice_attachment (id, notice_id, file_name, file_url, file_type, file_size, sort, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (9, 7, '大作业评分标准.docx', 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/notice/2025/11/03/87933cbe966f424da86bab7e1ffaae2a.docx', 'docx', 14405, 1, null, 'admin', '2025-11-07 15:02:16', 'admin', '2025-11-07 15:02:16', '0');
