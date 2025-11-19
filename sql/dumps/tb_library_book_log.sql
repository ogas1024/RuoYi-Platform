create table tb_library_book_log
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    book_id     bigint           not null comment '图书ID（tb_library_book.id）',
    action      varchar(32)      not null comment '动作（CREATE/EDIT/SUBMIT/APPROVE/REJECT/OFFLINE/ONLINE/DOWNLOAD/DELETE/HARD_DELETE）',
    actor_id    bigint           null comment '操作者ID',
    actor_name  varchar(64)      null comment '操作者名称',
    ip          varchar(64)      null comment 'IP',
    ua          varchar(256)     null comment 'UA',
    detail      varchar(1000)    null comment '详情/备注',
    result      varchar(16)      null comment '结果（SUCCESS/FAIL）',
    create_time datetime         null comment '时间',
    del_flag    char default '0' not null comment '删除标志'
)
    comment '数字图书馆-操作日志' charset = utf8mb4;

create index idx_book_time
    on tb_library_book_log (book_id, create_time);

INSERT INTO ruoyi.tb_library_book_log (id, book_id, action, actor_id, actor_name, ip, ua, detail, result, create_time, del_flag) VALUES (1, 1, 'APPROVE', 2001, 'librarian01', null, null, '审核通过', 'SUCCESS', '2025-11-05 15:50:21', '0');
