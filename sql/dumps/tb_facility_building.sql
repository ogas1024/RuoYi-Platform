create table tb_facility_building
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    building_name varchar(128)            not null comment '楼房名称（唯一）',
    status        char        default '0' not null comment '状态（0正常 1停用）',
    remark        varchar(500)            null comment '备注',
    create_by     varchar(64) default ''  not null comment '创建者',
    create_time   datetime                null comment '创建时间',
    update_by     varchar(64) default ''  not null comment '更新者',
    update_time   datetime                null comment '更新时间',
    del_flag      char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_building_name
        unique (building_name)
)
    comment '楼房表（功能房上级维度）' charset = utf8mb4;

INSERT INTO ruoyi.tb_facility_building (id, building_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (1, '综合楼A', '0', '教学与活动综合楼', '', null, '', null, '0');
INSERT INTO ruoyi.tb_facility_building (id, building_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (2, '图书馆B', '0', '图书馆与学术服务中心', '', null, '', null, '0');
INSERT INTO ruoyi.tb_facility_building (id, building_name, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (3, 'tst', '0', '', 'admin', null, '', null, '0');
