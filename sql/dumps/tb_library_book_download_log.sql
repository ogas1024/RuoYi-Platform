create table tb_library_book_download_log
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    book_id     bigint           not null comment '图书ID（tb_library_book.id）',
    asset_id    bigint           null comment '资产ID（可空）',
    user_id     bigint           not null comment '用户ID',
    result      char default '0' not null comment '结果（0成功 1失败）',
    ip          varchar(64)      null comment '来源IP',
    ua          varchar(256)     null comment 'User-Agent 截断',
    referer     varchar(256)     null comment '来源页',
    create_time datetime         null comment '时间'
)
    comment '数字图书馆-下载日志' charset = utf8mb4;

create index idx_book_time
    on tb_library_book_download_log (book_id, create_time);

create index idx_user_time
    on tb_library_book_download_log (user_id, create_time);

INSERT INTO ruoyi.tb_library_book_download_log (id, book_id, asset_id, user_id, result, ip, ua, referer, create_time) VALUES (1, 1, 1, 1003, '0', '127.0.0.1', 'curl/7.79', '/p/book', '2025-11-05 15:50:20');
INSERT INTO ruoyi.tb_library_book_download_log (id, book_id, asset_id, user_id, result, ip, ua, referer, create_time) VALUES (2, 1, null, 1, '0', '127.0.0.1', 'Apifox/1.0.0 (https://apifox.com)', null, '2025-11-05 20:12:55');
INSERT INTO ruoyi.tb_library_book_download_log (id, book_id, asset_id, user_id, result, ip, ua, referer, create_time) VALUES (3, 1, null, 1, '0', '127.0.0.1', 'Apifox/1.0.0 (https://apifox.com)', null, '2025-11-05 20:13:46');
INSERT INTO ruoyi.tb_library_book_download_log (id, book_id, asset_id, user_id, result, ip, ua, referer, create_time) VALUES (4, 1, null, 1, '0', '127.0.0.1', 'Apifox/1.0.0 (https://apifox.com)', null, '2025-11-05 20:14:04');
INSERT INTO ruoyi.tb_library_book_download_log (id, book_id, asset_id, user_id, result, ip, ua, referer, create_time) VALUES (5, 8, null, 110, '0', '127.0.0.1', 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36', 'http://localhost:5173/portal/library/detail?id=8', '2025-11-05 23:55:32');
INSERT INTO ruoyi.tb_library_book_download_log (id, book_id, asset_id, user_id, result, ip, ua, referer, create_time) VALUES (6, 9, null, 110, '0', '127.0.0.1', 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36', 'http://localhost:5173/portal/library/detail?id=9', '2025-11-06 00:10:38');
INSERT INTO ruoyi.tb_library_book_download_log (id, book_id, asset_id, user_id, result, ip, ua, referer, create_time) VALUES (7, 9, null, 110, '0', '127.0.0.1', 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36', 'http://localhost:5173/portal/library/list', '2025-11-06 00:11:25');
INSERT INTO ruoyi.tb_library_book_download_log (id, book_id, asset_id, user_id, result, ip, ua, referer, create_time) VALUES (8, 9, null, 110, '0', '127.0.0.1', 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36', 'http://localhost:5173/portal/library/list', '2025-11-06 00:18:01');
INSERT INTO ruoyi.tb_library_book_download_log (id, book_id, asset_id, user_id, result, ip, ua, referer, create_time) VALUES (9, 1, null, 1, '0', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'http://localhost:5173/portal/library/list', '2025-11-19 15:38:25');
INSERT INTO ruoyi.tb_library_book_download_log (id, book_id, asset_id, user_id, result, ip, ua, referer, create_time) VALUES (10, 9, null, 1, '0', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'http://localhost:5173/portal/library/list', '2025-11-19 15:38:51');
INSERT INTO ruoyi.tb_library_book_download_log (id, book_id, asset_id, user_id, result, ip, ua, referer, create_time) VALUES (11, 8, null, 1, '0', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'http://localhost:5173/portal/library/list', '2025-11-19 15:39:08');
INSERT INTO ruoyi.tb_library_book_download_log (id, book_id, asset_id, user_id, result, ip, ua, referer, create_time) VALUES (12, 10, null, 1, '0', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'http://localhost:5173/portal/library/list', '2025-11-19 15:46:49');
