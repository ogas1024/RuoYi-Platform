```sql
/* ======================================================================
   功能房预约模块 DDL（v1，MVP）
   约定与说明：
   - 表前缀统一 tb_；审计与软删字段统一。
   - 不限制时间粒度；业务层校验不与他人重叠（与 pending/approved/ongoing 比较）。
   - 最长使用时长：全局配置（分钟），默认 4320（72 小时）。
   - 审核开关：模块内配置表维护（audit_required）。
   - 冲突防护：提供幂等唯一键（room_id, applicant_id, start_time, end_time），并建议业务层加事务二次校验。
   - 外键采用逻辑约束 + 索引（不建硬外键，便于演进）。
====================================================================== */

-- ----------------------------
-- 楼房：tb_facility_building
-- ----------------------------
DROP TABLE IF EXISTS `tb_facility_building`;
CREATE TABLE `tb_facility_building` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `building_name`VARCHAR(128) NOT NULL COMMENT '楼房名称（唯一）',
  `status`       CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark`       VARCHAR(500)          DEFAULT NULL COMMENT '备注',
  `create_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`  DATETIME              DEFAULT NULL COMMENT '创建时间',
  `update_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`  DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_building_name` (`building_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼房表（功能房上级维度）';

-- ----------------------------
-- 功能房：tb_facility_room
-- ----------------------------
DROP TABLE IF EXISTS `tb_facility_room`;
CREATE TABLE `tb_facility_room` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `building_id`    BIGINT       NOT NULL COMMENT '楼房ID（逻辑外键）',
  `floor_no`       INT          NOT NULL COMMENT '楼层（可为负数表示地下一层）',
  `room_name`      VARCHAR(128) NOT NULL COMMENT '房间名称/编号',
  `capacity`       INT                   DEFAULT NULL COMMENT '容量（人数）',
  `equipment_tags` VARCHAR(255)          DEFAULT NULL COMMENT '设备/标签（逗号分隔）',
  `status`         CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态（0启用 1禁用）',
  `remark`         VARCHAR(500)          DEFAULT NULL COMMENT '备注',
  `create_by`      VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`    DATETIME              DEFAULT NULL COMMENT '创建时间',
  `update_by`      VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`    DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`       CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_room_name` (`building_id`,`floor_no`,`room_name`),
  KEY `idx_room_building_floor` (`building_id`,`floor_no`),
  KEY `idx_room_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功能房表';

-- ----------------------------
-- 预约记录：tb_facility_booking
-- 状态约定：0-待审核 pending 1-已批准 approved 2-已驳回 rejected
--           3-已取消 cancelled 4-进行中 ongoing 5-已完成 completed
-- ----------------------------
DROP TABLE IF EXISTS `tb_facility_booking`;
CREATE TABLE `tb_facility_booking` (
  `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_id`          BIGINT       NOT NULL COMMENT '功能房ID',
  `applicant_id`     BIGINT       NOT NULL COMMENT '预约申请人用户ID',
  `purpose`          VARCHAR(200) NOT NULL COMMENT '使用目的（1~200字）',
  `start_time`       DATETIME     NOT NULL COMMENT '开始时间（30分钟粒度）',
  `end_time`         DATETIME     NOT NULL COMMENT '结束时间（>开始时间，≤全局上限）',
  `duration_minutes` INT          NOT NULL COMMENT '预约时长（分钟）',
  
  `status`           CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态（0待审 1已批准 2已驳回 3已取消 4进行中 5已完成）',
  `reject_reason`    VARCHAR(500)          DEFAULT NULL COMMENT '驳回理由（必填于驳回）',
  `create_by`        VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`      DATETIME              DEFAULT NULL COMMENT '创建时间',
  `update_by`        VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`      DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`         CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_booking_once` (`room_id`,`applicant_id`,`start_time`,`end_time`),
  KEY `idx_booking_room_time` (`room_id`,`start_time`,`end_time`),
  KEY `idx_booking_applicant_time` (`applicant_id`,`start_time`),
  KEY `idx_booking_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功能房预约表';

-- ----------------------------
-- 预约-使用人关联：tb_facility_booking_user
-- 约束：同一 booking 去重；至少 3 人（含申请人）由业务层校验
-- ----------------------------
DROP TABLE IF EXISTS `tb_facility_booking_user`;
CREATE TABLE `tb_facility_booking_user` (
  `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `booking_id` BIGINT      NOT NULL COMMENT '预约ID',
  `user_id`    BIGINT      NOT NULL COMMENT '用户ID',
  `is_applicant` CHAR(1)   NOT NULL DEFAULT '0' COMMENT '是否申请人（1是 0否）',
  `create_by`  VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`DATETIME             DEFAULT NULL COMMENT '创建时间',
  `update_by`  VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`DATETIME             DEFAULT NULL COMMENT '更新时间',
  `del_flag`   CHAR(1)     NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_booking_user` (`booking_id`,`user_id`),
  KEY `idx_booking_user_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约使用人关联表';

-- ----------------------------
-- 模块设置：tb_facility_setting（单行配置）
-- ----------------------------
DROP TABLE IF EXISTS `tb_facility_setting`;
CREATE TABLE `tb_facility_setting` (
  `id`                   BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID（建议固定为1）',
  `audit_required`       CHAR(1)     NOT NULL DEFAULT '1' COMMENT '是否需要审核（1是 0否）',
  `max_duration_minutes` INT         NOT NULL DEFAULT 4320 COMMENT '最长使用时长（分钟，全局）',
  `remark`               VARCHAR(500)         DEFAULT NULL COMMENT '备注',
  `create_by`            VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`          DATETIME             DEFAULT NULL COMMENT '创建时间',
  `update_by`            VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`          DATETIME             DEFAULT NULL COMMENT '更新时间',
  `del_flag`             CHAR(1)     NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功能房模块设置';

-- ----------------------------
-- 申请人封禁：tb_facility_ban
-- 说明：仅限制用户作为“申请人”提交预约，允许其作为“使用人”参与
-- ----------------------------
DROP TABLE IF EXISTS `tb_facility_ban`;
CREATE TABLE `tb_facility_ban` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`     BIGINT       NOT NULL COMMENT '用户ID（申请人）',
  `reason`      VARCHAR(500) NOT NULL COMMENT '封禁原因',
  `status`      CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态（0生效 1失效）',
  `expire_time` DATETIME              DEFAULT NULL COMMENT '到期时间（可选）',
  `create_by`   VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME              DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`    CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ban_user` (`user_id`),
  UNIQUE KEY `uk_ban_user_status` (`user_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申请人封禁表';

-- ----------------------------
-- 示例数据（可按需调整/清空）
-- ----------------------------
INSERT INTO `tb_facility_building` (`building_name`,`status`,`remark`) VALUES
('综合楼A','0','教学与活动综合楼'),
('图书馆B','0','图书馆与学术服务中心');

INSERT INTO `tb_facility_room` (`building_id`,`floor_no`,`room_name`,`capacity`,`equipment_tags`,`status`,`remark`) VALUES
(1,1,'报告厅101',80,'多媒体,投影','0','大型报告与活动'),
(1,2,'会议室201',20,'白板,电视','0','小型会议'),
(2,3,'研讨室301',12,'圆桌,白板','0','研讨/教学');

INSERT INTO `tb_facility_setting` (`id`,`audit_required`,`max_duration_minutes`,`remark`) VALUES
(1,'1',4320,'默认开启审核；最长 72 小时');

-- 示例预约：2025-11-10 09:00~12:00 已批准
INSERT INTO `tb_facility_booking` (`room_id`,`applicant_id`,`purpose`,`start_time`,`end_time`,`duration_minutes`,`status`) VALUES
(2,1001,'课程讨论会','2025-11-10 09:00:00','2025-11-10 12:00:00',180,'1');

-- 示例使用人（含申请人）
INSERT INTO `tb_facility_booking_user` (`booking_id`,`user_id`,`is_applicant`) VALUES
(1,1001,'1'),(1,1002,'0'),(1,1003,'0');

```