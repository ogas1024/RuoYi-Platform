create table tb_library_book_asset
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    book_id        bigint                  not null comment '图书ID（tb_library_book.id）',
    asset_type     char                    not null comment '资产类型（0文件 1外链）',
    format         varchar(16)             null comment '文件格式（pdf/epub/mobi/zip/other）',
    file_url       varchar(512)            null comment '文件URL（OSS/网关）',
    file_size      bigint                  null comment '文件大小（字节）',
    file_hash      varchar(128)            null comment '文件哈希（SHA-256）',
    oss_object_key varchar(512)            null comment 'OSS 对象键（用于删除）',
    link_url       varchar(512)            null comment '外链URL',
    sort           int         default 0   not null comment '排序（下载时的优先顺序）',
    create_by      varchar(64) default ''  not null comment '创建者',
    create_time    datetime                null comment '创建时间',
    update_by      varchar(64) default ''  not null comment '更新者',
    update_time    datetime                null comment '更新时间',
    del_flag       char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_library_book_file
        unique (book_id, asset_type, format, file_hash),
    constraint uk_library_book_link
        unique (book_id, asset_type, link_url),
    constraint fk_library_asset_book
        foreign key (book_id) references tb_library_book (id)
            on delete cascade
)
    comment '数字图书馆-图书资产（多格式/外链）' charset = utf8mb4;

create index idx_book_id
    on tb_library_book_asset (book_id);

INSERT INTO ruoyi.tb_library_book_asset (id, book_id, asset_type, format, file_url, file_size, file_hash, oss_object_key, link_url, sort, create_by, create_time, update_by, update_time, del_flag) VALUES (1, 1, '0', 'pdf', 'https://oss.example.com/books/networks7.pdf', 10485760, 'sha256:demo01', 'books/networks7.pdf', null, 1, 'alice', '2025-11-05 15:50:19', 'alice', '2025-11-05 15:50:19', '0');
INSERT INTO ruoyi.tb_library_book_asset (id, book_id, asset_type, format, file_url, file_size, file_hash, oss_object_key, link_url, sort, create_by, create_time, update_by, update_time, del_flag) VALUES (2, 1, '1', null, null, null, null, null, 'https://example.com/networks7', 2, 'alice', '2025-11-05 15:50:20', 'alice', '2025-11-05 15:50:20', '0');
INSERT INTO ruoyi.tb_library_book_asset (id, book_id, asset_type, format, file_url, file_size, file_hash, oss_object_key, link_url, sort, create_by, create_time, update_by, update_time, del_flag) VALUES (3, 8, '0', 'pdf', 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/library/temp/2025/11/05/f9a66dc93fc34ab9b549d3bb3d7a03c8.pdf', 463727, 'sha256:ffe7d8e166866fefe9d16ba87e4f4a4e0d9e43f75a7085d9e00d4ce43f258d7d', null, null, 0, 'admin', '2025-11-05 22:57:03', '', null, '0');
INSERT INTO ruoyi.tb_library_book_asset (id, book_id, asset_type, format, file_url, file_size, file_hash, oss_object_key, link_url, sort, create_by, create_time, update_by, update_time, del_flag) VALUES (8, 9, '0', 'pdf', 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/library/9/2025/11/06/195e0398671a44cc8a9526ee27ffb906.pdf', 331517, 'sha256:63027f33017f9dfd9b80d086e146aae8df56a37caadf2a539a9a218d34ef44b5', null, null, 0, 'ogas', '2025-11-06 10:13:08', '', null, '0');
INSERT INTO ruoyi.tb_library_book_asset (id, book_id, asset_type, format, file_url, file_size, file_hash, oss_object_key, link_url, sort, create_by, create_time, update_by, update_time, del_flag) VALUES (9, 10, '0', 'pdf', 'https://ty-ruoyi.oss-cn-shanghai.aliyuncs.com/uploads/library/pdf/2025/11/08/c4e4b765628043be85560cadffc25fdc.pdf', 611522, 'sha256:6c65975ca9e897f89fa49dfa73d822071d9c76de80fca2226b75d70837271fa6', null, null, 0, 'admin', '2025-11-08 15:16:53', '', null, '0');
