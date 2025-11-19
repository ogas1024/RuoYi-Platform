create table tb_facility_room
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    building_id    bigint                  not null comment '楼房ID（逻辑外键）',
    floor_no       int                     not null comment '楼层（可为负数表示地下一层）',
    room_name      varchar(128)            not null comment '房间名称/编号',
    capacity       int                     null comment '容量（人数）',
    equipment_tags varchar(255)            null comment '设备/标签（逗号分隔）',
    status         char        default '0' not null comment '状态（0启用 1禁用）',
    remark         varchar(500)            null comment '备注',
    create_by      varchar(64) default ''  not null comment '创建者',
    create_time    datetime                null comment '创建时间',
    update_by      varchar(64) default ''  not null comment '更新者',
    update_time    datetime                null comment '更新时间',
    del_flag       char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_room_name
        unique (building_id, floor_no, room_name)
)
    comment '功能房表' charset = utf8mb4;

create index idx_room_building_floor
    on tb_facility_room (building_id, floor_no);

create index idx_room_status
    on tb_facility_room (status);

INSERT INTO ruoyi.tb_facility_room (id, building_id, floor_no, room_name, capacity, equipment_tags, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (1, 1, 1, '报告厅101', 80, '多媒体,投影', '0', '大型报告与活动', '', null, '', '2025-11-06 17:17:30', '0');
INSERT INTO ruoyi.tb_facility_room (id, building_id, floor_no, room_name, capacity, equipment_tags, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (2, 1, 2, '会议室201', 20, '白板,电视', '0', '小型会议', '', null, '', null, '0');
INSERT INTO ruoyi.tb_facility_room (id, building_id, floor_no, room_name, capacity, equipment_tags, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (3, 2, 3, '研讨室301', 12, '圆桌,白板', '0', '研讨/教学', '', null, '', null, '0');
INSERT INTO ruoyi.tb_facility_room (id, building_id, floor_no, room_name, capacity, equipment_tags, status, remark, create_by, create_time, update_by, update_time, del_flag) VALUES (4, 3, 1, 'gggg', null, '', '0', '', 'admin', null, '', null, '0');
