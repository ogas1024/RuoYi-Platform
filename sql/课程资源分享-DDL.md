```sql
/* ======================================================================
   课程资源分享模块（v2）
   日期：2025-10-18
   重要说明：
   - 结构差异：
       1) tb_major 移除 dept_id 与 sort，完全独立于 RuoYi 部门体系。
       2) tb_course 移除 sort。
       3) tb_course_resource 移除 file_ext（扩展名在应用层校验）。
   - 约定：仅允许压缩包（zip/rar/7z/tar/tar.gz/tar.bz2/tar.xz）或外链；大小≤100MB 由应用层控制。
   - 状态流转：0-待审 1-已通过 2-驳回 3-已下架；修改后重新进入待审。
====================================================================== */

-- ----------------------------
-- 专业：tb_major（独立维护，不依赖 sys_dept）
-- 关键点：
--  - 使用专业名唯一索引，便于后台维护与前端展示。
--  - 管理员通过后台页面对专业进行增删改查。
-- ----------------------------
DROP TABLE IF EXISTS `tb_major`;
CREATE TABLE `tb_major` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `major_name`   VARCHAR(128) NOT NULL COMMENT '专业名称（唯一）',
  `status`       CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark`       VARCHAR(500)          DEFAULT NULL COMMENT '备注',
  `create_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`  DATETIME              DEFAULT NULL COMMENT '创建时间',
  `update_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`  DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_major_name` (`major_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业表（独立）';

-- ----------------------------
-- 课程：tb_course
-- 关键点：
--  - 与专业一对多；同一专业下课程名唯一。
-- ----------------------------
DROP TABLE IF EXISTS `tb_course`;
CREATE TABLE `tb_course` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `major_id`     BIGINT        NOT NULL COMMENT '所属专业ID',
  `course_name`  VARCHAR(255)  NOT NULL COMMENT '课程名称',
  `course_code`  VARCHAR(64)            DEFAULT NULL COMMENT '课程编码（可选）',
  `status`       CHAR(1)       NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark`       VARCHAR(500)           DEFAULT NULL COMMENT '备注',
  `create_by`    VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`  DATETIME               DEFAULT NULL COMMENT '创建时间',
  `update_by`    VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`  DATETIME               DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)       NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_course_unique` (`major_id`, `course_name`),
  KEY `idx_course_major` (`major_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- ----------------------------
-- 专业负责人映射：tb_major_lead
-- 关键点：
--  - 管理员在后台维护负责人（增/删），支持一专业多负责人。
--  - 用于限制负责人后台可见与可操作的数据范围。
-- ----------------------------
DROP TABLE IF EXISTS `tb_major_lead`;
CREATE TABLE `tb_major_lead` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `major_id`     BIGINT       NOT NULL COMMENT '专业ID',
  `user_id`      BIGINT       NOT NULL COMMENT '负责人用户ID',
  `remark`       VARCHAR(500)          DEFAULT NULL COMMENT '备注',
  `create_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`  DATETIME              DEFAULT NULL COMMENT '创建时间',
  `update_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`  DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_major_user` (`major_id`, `user_id`),
  KEY `idx_lead_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业负责人映射表';

/*
----------------------------------------------------------------------
  日期：2025-11-04
  模块：课程资源分享 / 专业负责人联动
  说明：
    - 无 DDL 结构变更；保留 tb_major_lead 作为“用户-专业”映射表（唯一 (major_id,user_id)）。
    - 新增业务约定：当新增/删除 tb_major_lead 映射时，应用层需与 RuoYi 核心表联动：
        1) 新增时：若用户尚未拥有角色 key=major_lead，则插入 sys_user_role 赋予角色；
        2) 删除时：若用户不再承担任何专业负责人（tb_major_lead 计数=0），则从 sys_user_role 撤销该角色。
    - 角色查找：通过 sys_role.role_key = 'major_lead' 获取 role_id。
----------------------------------------------------------------------
*/

-- ----------------------------
-- 课程资源：tb_course_resource
-- 关键点：
--  - 资源类型 0-文件 1-外链；仅压缩包或外链（扩展名不入库）。
--  - 去重：文件按 (course_id, resource_type, file_hash)；外链按 (course_id, resource_type, link_url)。
--  - 下载计数通过 /download 接口 + Redis 自增（落库见 update_time/last_download_time）。
-- ----------------------------
DROP TABLE IF EXISTS `tb_course_resource`;
CREATE TABLE `tb_course_resource` (
  `id`                 BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `major_id`           BIGINT        NOT NULL COMMENT '专业ID（冗余便于索引）',
  `course_id`          BIGINT        NOT NULL COMMENT '课程ID',
  `resource_name`      VARCHAR(255)  NOT NULL COMMENT '资源名称（用户自定义）',
  `resource_type`      TINYINT       NOT NULL COMMENT '资源类型：0-文件 1-外链',
  `file_url`           VARCHAR(512)           DEFAULT NULL COMMENT '文件直链（OSS，resource_type=0时必填）',
  `file_hash`          VARCHAR(128)           DEFAULT NULL COMMENT '文件哈希（SHA-256，resource_type=0时必填）',
  `file_size`          BIGINT                 DEFAULT NULL COMMENT '文件大小（字节）',
  `link_url`           VARCHAR(512)           DEFAULT NULL COMMENT '外链URL（resource_type=1时必填）',
  `description`        TEXT           NOT NULL COMMENT '资源简介（必填）',
  `status`             TINYINT        NOT NULL DEFAULT 0 COMMENT '状态：0待审 1已通过 2驳回 3已下架',
  `audit_by`           VARCHAR(64)             DEFAULT NULL COMMENT '审核人',
  `audit_time`         DATETIME                DEFAULT NULL COMMENT '审核时间',
  `audit_reason`       VARCHAR(512)            DEFAULT NULL COMMENT '驳回理由',
  `publish_time`       DATETIME                DEFAULT NULL COMMENT '首次上架时间',
  `download_count`     INT            NOT NULL DEFAULT 0 COMMENT '下载次数',
  `last_download_time` DATETIME                DEFAULT NULL COMMENT '最近下载时间',
  `uploader_id`        BIGINT         NOT NULL COMMENT '上传者用户ID',
  `uploader_name`      VARCHAR(64)    NOT NULL COMMENT '上传者名称（冗余）',
  `create_by`          VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`        DATETIME                DEFAULT NULL COMMENT '创建时间',
  `update_by`          VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`        DATETIME                DEFAULT NULL COMMENT '更新时间',
  `del_flag`           CHAR(1)        NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_course_filehash` (`course_id`, `resource_type`, `file_hash`),
  UNIQUE KEY `uk_course_linkurl`  (`course_id`, `resource_type`, `link_url`(191)),
  KEY `idx_course_status` (`course_id`, `status`),
  KEY `idx_status_time` (`status`, `create_time`),
  KEY `idx_download_count` (`download_count`),
  KEY `idx_uploader_id` (`uploader_id`),
  KEY `idx_major_course` (`major_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资源表（仅压缩包或外链）';

/*
----------------------------------------------------------------------
  日期：2025-11-04（修订）
  模块：课程资源分享 / 最佳推荐（改为独立表实现）
  重要说明：
    - 为降低主表变更成本，本版取消对 tb_course_resource 的 ALTER，改为独立表。
    - 请忽略此前“为资源表增加 is_best/best_by/best_time”的建议；
    - 通过映射表 tb_course_resource_best 记录最佳标记，查询通过 LEFT JOIN 计算 is_best。
----------------------------------------------------------------------
*/
DROP TABLE IF EXISTS `tb_course_resource_best`;
CREATE TABLE `tb_course_resource_best` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id`  BIGINT       NOT NULL COMMENT '资源ID（唯一）',
  `best_by`      VARCHAR(64)  NOT NULL COMMENT '最佳标记人',
  `best_time`    DATETIME     NOT NULL COMMENT '最佳标记时间',
  `create_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`  DATETIME              DEFAULT NULL COMMENT '创建时间',
  `update_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`  DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_best_resource` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资源最佳标记表';

-- ----------------------------
-- 审计日志：tb_course_resource_log
-- 关键点：
--  - 记录全生命周期关键动作，便于追责与统计。
-- ----------------------------
DROP TABLE IF EXISTS `tb_course_resource_log`;
CREATE TABLE `tb_course_resource_log` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id`  BIGINT       NOT NULL COMMENT '资源ID',
  `action`       VARCHAR(32)  NOT NULL COMMENT 'CREATE/EDIT/APPROVE/REJECT/OFFLINE/ONLINE/DOWNLOAD/DELETE/HARD_DELETE',
  `actor_id`     BIGINT                DEFAULT NULL COMMENT '操作者ID',
  `actor_name`   VARCHAR(64)           DEFAULT NULL COMMENT '操作者名称',
  `ip`           VARCHAR(64)           DEFAULT NULL COMMENT 'IP地址',
  `user_agent`   VARCHAR(255)          DEFAULT NULL COMMENT 'User-Agent',
  `detail`       TEXT                  DEFAULT NULL COMMENT '详情（JSON文本）',
  `result`       VARCHAR(16)           DEFAULT NULL COMMENT 'SUCCESS/FAIL',
  `create_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`  DATETIME              DEFAULT NULL COMMENT '创建时间',
  `update_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`  DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_log_resource_time` (`resource_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资源操作日志表（审计）';

/* ----------------------------
   示例基础数据（按需执行）
   - 预置10个专业（不含“公共课”）。
   - 选取少量课程示例便于联调。
----------------------------- */

-- 专业（10个）
INSERT INTO `tb_major` (`id`, `major_name`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES
  (1,  '电气工程及其自动化', '0', '培养专业', 'system', NOW(), '', NULL, '0'),
  (2,  '自动化',             '0', '培养专业', 'system', NOW(), '', NULL, '0'),
  (3,  '通信工程',           '0', '培养专业', 'system', NOW(), '', NULL, '0'),
  (4,  '计算机科学与技术',   '0', '培养专业', 'system', NOW(), '', NULL, '0'),
  (5,  '土木工程',           '0', '培养专业', 'system', NOW(), '', NULL, '0'),
  (6,  '机械设计制造及其自动化', '0', '培养专业', 'system', NOW(), '', NULL, '0'),
  (7,  '车辆工程',           '0', '培养专业', 'system', NOW(), '', NULL, '0'),
  (8,  '交通运输',           '0', '培养专业', 'system', NOW(), '', NULL, '0'),
  (9,  '会计学',             '0', '培养专业', 'system', NOW(), '', NULL, '0'),
  (10, '人力资源管理',       '0', '培养专业', 'system', NOW(), '', NULL, '0');

-- 课程示例（少量）
INSERT INTO `tb_course` (`id`, `major_id`, `course_name`, `course_code`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES
  (4001, 1, '电路原理',   'EE-CP',   '0', '', 'system', NOW(), '', NULL, '0'),
  (3001, 3, '信号与系统', 'CE-SS',   '0', '', 'system', NOW(), '', NULL, '0'),
  (4101, 4, '数据结构',   'CS-DS',   '0', '', 'system', NOW(), '', NULL, '0'),
  (4102, 4, '操作系统',   'CS-OS',   '0', '', 'system', NOW(), '', NULL, '0');

-- 课程资源示例（文件型）
INSERT INTO `tb_course_resource`
(`id`,`major_id`,`course_id`,`resource_name`,`resource_type`,`file_url`,`file_hash`,`file_size`,`link_url`,`description`,`status`,`audit_by`,`audit_time`,`audit_reason`,`publish_time`,`download_count`,`last_download_time`,`uploader_id`,`uploader_name`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`)
VALUES
  (10001, 4, 4101, '数据结构-资料包示例', 0,
   'https://oss-example/bucket/cs/ds/sample.zip',
   'sha256:0000000000000000000000000000000000000000000000000000000000000000',
   102400, NULL,
   '示例数据：包含笔记与课件', 1,
   'admin', NOW(), NULL, NOW(),
   5, NOW(), 1, 'admin', 'admin', NOW(), 'admin', NOW(), '0');

-- 日志示例
INSERT INTO `tb_course_resource_log` (`resource_id`, `action`, `actor_id`, `actor_name`, `ip`, `user_agent`, `detail`, `result`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES
  (10001, 'CREATE', 1, 'admin', '127.0.0.1', 'Mozilla/5.0', '{"note":"初始化导入示例"}', 'SUCCESS', 'admin', NOW(), 'admin', NOW(), '0'),
  (10001, 'APPROVE', 1, 'admin', '127.0.0.1', 'Mozilla/5.0', '{"note":"审核通过"}', 'SUCCESS', 'admin', NOW(), 'admin', NOW(), '0');


/*
======================================================================
  变更记录（按日期追加）
----------------------------------------------------------------------
  日期：2025-11-04
  模块：课程资源分享 / 积分与用户排行榜（CR 专属）
  说明：
    - 新增两张业务表（仅供“课程资源分享”模块使用，避免与“数字图书馆”等后续积分表冲突）：
      (1) tb_cr_user_score：用户积分（聚合，按用户×专业；major_id=0 表示全站汇总）。
      (2) tb_cr_user_score_log：用户积分变动流水（一次性发放防刷，唯一 (user_id, resource_id, event_type)）。
    - 规则：
      * 仅在“第一次审核通过”与“第一次被设置为最佳”发放积分；
      * 取消“最佳”不扣分；再次审核通过/再次设置为最佳不重复加分；
      * 通过在 tb_user_score_log 建立唯一约束实现幂等；
      * 聚合表 tb_user_score 维护 (user_id, major_id=资源所属专业 或 0=全站) 的积分与计数，便于排行榜查询；
      * 后续可将发放分值作为应用配置（MVP 采用固定值：通过+5，最佳+10）。
======================================================================
*/

-- ----------------------------
-- 用户积分聚合：tb_cr_user_score（CR 专属）
-- 关键点：
--  - (user_id, major_id) 唯一；major_id=0 表示全站总积分行
--  - total_score 降序用于排行榜；approve_count/best_count 仅统计次数
-- ----------------------------
DROP TABLE IF EXISTS `tb_cr_user_score`;
CREATE TABLE `tb_cr_user_score` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`       BIGINT       NOT NULL COMMENT '用户ID（sys_user.id）',
  `username`      VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '用户名快照（冗余）',
  `major_id`      BIGINT       NOT NULL DEFAULT 0 COMMENT '专业ID；0=全站',
  `total_score`   INT          NOT NULL DEFAULT 0 COMMENT '累计积分',
  `approve_count` INT          NOT NULL DEFAULT 0 COMMENT '审核通过次数（仅第一次通过计数）',
  `best_count`    INT          NOT NULL DEFAULT 0 COMMENT '被评为最佳次数（仅第一次计数）',
  `remark`        VARCHAR(255)          DEFAULT NULL COMMENT '备注',
  `create_by`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`   DATETIME              DEFAULT NULL COMMENT '创建时间',
  `update_by`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`   DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`      CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_major` (`user_id`, `major_id`),
  KEY `idx_major_score` (`major_id`, `total_score`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资源（CR）用户积分聚合表（用户×专业；0=全站）';

-- ----------------------------
-- 用户积分流水：tb_cr_user_score_log（CR 专属）
-- 关键点：
--  - 幂等防刷：(user_id, resource_id, event_type) 唯一；
--  - event_type：APPROVE/BEST；delta 为正数；取消“最佳”不记录负数流水；
-- ----------------------------
DROP TABLE IF EXISTS `tb_cr_user_score_log`;
CREATE TABLE `tb_cr_user_score_log` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`      BIGINT       NOT NULL COMMENT '得分用户ID（资源上传者）',
  `username`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '用户名快照（冗余）',
  `major_id`     BIGINT       NOT NULL DEFAULT 0 COMMENT '资源所属专业ID；0=全站',
  `resource_id`  BIGINT       NOT NULL COMMENT '资源ID（tb_course_resource.id）',
  `event_type`   VARCHAR(16)  NOT NULL COMMENT 'APPROVE/BEST',
  `delta`        INT          NOT NULL COMMENT '积分变动（正数）',
  `remark`       VARCHAR(255)          DEFAULT NULL COMMENT '备注',
  `create_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`  DATETIME              DEFAULT NULL COMMENT '创建时间',
  `update_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`  DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_once_event` (`user_id`, `resource_id`, `event_type`),
  KEY `idx_user_time` (`user_id`, `create_time`),
  KEY `idx_major_time` (`major_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资源（CR）用户积分变动流水（一次性发放）';
```