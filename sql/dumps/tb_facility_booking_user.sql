create table tb_facility_booking_user
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    booking_id   bigint                  not null comment '预约ID',
    user_id      bigint                  not null comment '用户ID',
    is_applicant char        default '0' not null comment '是否申请人（1是 0否）',
    create_by    varchar(64) default ''  not null comment '创建者',
    create_time  datetime                null comment '创建时间',
    update_by    varchar(64) default ''  not null comment '更新者',
    update_time  datetime                null comment '更新时间',
    del_flag     char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_booking_user
        unique (booking_id, user_id)
)
    comment '预约使用人关联表' charset = utf8mb4;

create index idx_booking_user_user
    on tb_facility_booking_user (user_id);

INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (1, 1, 1001, '1', '', null, '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (2, 1, 1002, '0', '', null, '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (3, 1, 1003, '0', '', null, '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (4, 2, 110, '0', 'admin', '2025-11-06 16:10:01', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (5, 2, 111, '0', 'admin', '2025-11-06 16:10:01', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (6, 2, 112, '0', 'admin', '2025-11-06 16:10:01', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (7, 2, 1, '1', 'admin', '2025-11-06 16:10:01', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (8, 3, 110, '0', 'admin', '2025-11-06 16:15:53', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (9, 3, 111, '0', 'admin', '2025-11-06 16:15:53', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (10, 3, 112, '0', 'admin', '2025-11-06 16:15:53', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (11, 3, 1, '1', 'admin', '2025-11-06 16:15:53', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (12, 4, 110, '1', 'ogas', '2025-11-06 16:38:23', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (13, 4, 111, '0', 'ogas', '2025-11-06 16:38:23', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (14, 4, 112, '0', 'ogas', '2025-11-06 16:38:23', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (15, 5, 110, '1', 'ogas', '2025-11-06 16:45:13', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (16, 5, 111, '0', 'ogas', '2025-11-06 16:45:13', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (17, 5, 112, '0', 'ogas', '2025-11-06 16:45:13', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (18, 6, 110, '0', 'demo', '2025-11-08 21:35:40', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (19, 6, 111, '0', 'demo', '2025-11-08 21:35:40', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (20, 6, 112, '0', 'demo', '2025-11-08 21:35:40', '', null, '0');
INSERT INTO ruoyi.tb_facility_booking_user (id, booking_id, user_id, is_applicant, create_by, create_time, update_by, update_time, del_flag) VALUES (21, 6, 114, '1', 'demo', '2025-11-08 21:35:40', '', null, '0');
