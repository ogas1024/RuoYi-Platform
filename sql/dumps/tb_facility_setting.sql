create table tb_facility_setting
(
    id                   bigint auto_increment comment '主键ID（建议固定为1）'
        primary key,
    audit_required       char        default '1'  not null comment '是否需要审核（1是 0否）',
    max_duration_minutes int         default 4320 not null comment '最长使用时长（分钟，全局）',
    remark               varchar(500)             null comment '备注',
    create_by            varchar(64) default ''   not null comment '创建者',
    create_time          datetime                 null comment '创建时间',
    update_by            varchar(64) default ''   not null comment '更新者',
    update_time          datetime                 null comment '更新时间',
    del_flag             char        default '0'  not null comment '删除标志（0存在 2删除）'
)
    comment '功能房模块设置' charset = utf8mb4;

INSERT INTO ruoyi.tb_facility_setting (id, audit_required, max_duration_minutes, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (1, '0', 4320, null, '', null, 'admin', '2025-11-06 16:42:59', '0');
