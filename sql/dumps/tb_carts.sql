create table tb_carts
(
    id           int auto_increment comment '主键'
        primary key,
    user_id      int                                 not null comment '用户ID',
    book_id      int                                 not null comment '图书ID',
    quantity     int       default 1                 not null comment '购买数量',
    created_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '购物车表' engine = MyISAM
                       collate = utf8mb4_general_ci
                       row_format = FIXED;

