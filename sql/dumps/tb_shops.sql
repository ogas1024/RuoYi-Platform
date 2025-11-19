create table tb_shops
(
    id             int          not null comment '对应Sys_User表的User_id'
        primary key,
    shop_name      varchar(255) null comment '店名',
    contact_person varchar(255) null comment '联系人',
    contact_phone  varchar(255) null comment '联系方式',
    category_id    int          null comment '主营类别 ID'
)
    comment '商铺信息表' engine = MyISAM
                         collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

