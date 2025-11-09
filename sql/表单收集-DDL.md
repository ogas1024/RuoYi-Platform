```sql
/* ======================================================================
   问卷（表单收集）模块（MVP）
   日期：2025-11-07
   说明：
   - 管理侧：列表/详情/创建/归档/延期。
   - 用户侧：可见范围内的问卷列表（未过期+已过期）/填写提交/我的填写（截止前可修改）。
   - 可见范围：支持角色/部门/岗位的并集（与公告模块一致）；visible_all=1 表示全员可见。
   - 题型：1文本 2单选 3多选；为“文件/时间”等扩展预留 ext_json；选项仅用于单选/多选题。
   - 约束：一个用户每份问卷仅允许一份提交（tb_survey_answer 唯一键）。
   - 状态：tb_survey.status — 0草稿 1发布 2已归档；归档后不可再提交；延期仅更新 deadline。
====================================================================== */

-- ----------------------------
-- 主表：tb_survey（问卷）
-- ----------------------------
DROP TABLE IF EXISTS `tb_survey`;
CREATE TABLE `tb_survey` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title`         VARCHAR(200)  NOT NULL COMMENT '问卷标题/表单名称',
  `status`        CHAR(1)       NOT NULL DEFAULT '1' COMMENT '状态：0草稿 1发布 2归档',
  `visible_all`   CHAR(1)       NOT NULL DEFAULT '1' COMMENT '是否全员可见：1是 0否（自定义范围）',
  `deadline`      DATETIME               DEFAULT NULL COMMENT '截止时间（截止后不可提交或修改）',
  `remark`        VARCHAR(500)           DEFAULT NULL COMMENT '备注',
  `pinned`        char        default '0' not null comment '是否置顶：0否 1是',
  `pinned_time`   datetime                null comment '置顶时间',
  `create_by`     VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`   DATETIME                DEFAULT NULL COMMENT '创建时间',
  `update_by`     VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`   DATETIME                DEFAULT NULL COMMENT '更新时间',
  `del_flag`      CHAR(1)        NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_status_deadline` (`status`, `deadline`),
  KEY `idx_create_by` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问卷-主表';

-- ----------------------------
-- 范围：tb_survey_scope（与公告一致：角色/部门/岗位并集）
-- ----------------------------
DROP TABLE IF EXISTS `tb_survey_scope`;
CREATE TABLE `tb_survey_scope` (
  `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `survey_id`  BIGINT      NOT NULL COMMENT '问卷ID（tb_survey.id）',
  `scope_type` TINYINT     NOT NULL COMMENT '范围类型：0角色 1部门 2岗位',
  `ref_id`     BIGINT      NOT NULL COMMENT '引用ID：sys_role.role_id / sys_dept.dept_id / sys_post.post_id',
  `create_by`  VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME            DEFAULT NULL COMMENT '创建时间',
  `update_by`  VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME            DEFAULT NULL COMMENT '更新时间',
  `del_flag`   CHAR(1)     NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_scope` (`survey_id`, `scope_type`, `ref_id`),
  KEY `idx_survey` (`survey_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问卷-可见范围';

-- ----------------------------
-- 题目：tb_survey_item
-- ----------------------------
DROP TABLE IF EXISTS `tb_survey_item`;
CREATE TABLE `tb_survey_item` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `survey_id`   BIGINT        NOT NULL COMMENT '问卷ID（tb_survey.id）',
  `title`       VARCHAR(300)  NOT NULL COMMENT '题目/提示',
  `type`        TINYINT       NOT NULL COMMENT '类型：1文本 2单选 3多选（预留4文件 5时间）',
  `required`    CHAR(1)       NOT NULL DEFAULT '0' COMMENT '是否必填：0否 1是',
  `sort_no`     INT           NOT NULL DEFAULT 0 COMMENT '排序号（0开始）',
  `ext_json`    JSON                   DEFAULT NULL COMMENT '扩展配置（占位，文件/时间等）',
  `create_by`   VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME               DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME               DEFAULT NULL COMMENT '更新时间',
  `del_flag`    CHAR(1)       NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_survey_sort` (`survey_id`, `sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问卷-题目';

-- ----------------------------
-- 选项：tb_survey_option（仅用于单选/多选题）
-- ----------------------------
DROP TABLE IF EXISTS `tb_survey_option`;
CREATE TABLE `tb_survey_option` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `item_id`     BIGINT        NOT NULL COMMENT '题目ID（tb_survey_item.id）',
  `label`       VARCHAR(200)  NOT NULL COMMENT '选项展示文本',
  `value`       VARCHAR(200)           DEFAULT NULL COMMENT '选项值（可空，默认=label）',
  `sort_no`     INT           NOT NULL DEFAULT 0 COMMENT '排序号',
  `create_by`   VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME               DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME               DEFAULT NULL COMMENT '更新时间',
  `del_flag`    CHAR(1)       NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_item_sort` (`item_id`, `sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问卷-选项';

-- ----------------------------
-- 答卷：tb_survey_answer（用户提交的一份问卷）
-- ----------------------------
DROP TABLE IF EXISTS `tb_survey_answer`;
CREATE TABLE `tb_survey_answer` (
  `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `survey_id`    BIGINT      NOT NULL COMMENT '问卷ID（tb_survey.id）',
  `user_id`      BIGINT      NOT NULL COMMENT '提交人用户ID（若依用户）',
  `submit_time`  DATETIME             DEFAULT NULL COMMENT '提交时间',
  `update_time2` DATETIME             DEFAULT NULL COMMENT '最近修改时间（命名避开审计字段）',
  `create_by`    VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`  DATETIME             DEFAULT NULL COMMENT '创建时间',
  `update_by`    VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`  DATETIME             DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)     NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_survey_user` (`survey_id`, `user_id`),
  KEY `idx_survey` (`survey_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问卷-答卷（每人一份）';

-- ----------------------------
-- 答案：tb_survey_answer_item（每道题的作答）
-- ----------------------------
DROP TABLE IF EXISTS `tb_survey_answer_item`;
CREATE TABLE `tb_survey_answer_item` (
  `id`             BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `answer_id`      BIGINT   NOT NULL COMMENT '答卷ID（tb_survey_answer.id）',
  `item_id`        BIGINT   NOT NULL COMMENT '题目ID（tb_survey_item.id）',
  `value_text`     TEXT              DEFAULT NULL COMMENT '文本题答案',
  `value_option_ids` JSON            DEFAULT NULL COMMENT '单/多选答案（JSON数组，元素为 tb_survey_option.id）',
  `create_by`      VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`    DATETIME            DEFAULT NULL COMMENT '创建时间',
  `update_by`      VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`    DATETIME            DEFAULT NULL COMMENT '更新时间',
  `del_flag`       CHAR(1)     NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_answer_item` (`answer_id`, `item_id`),
  KEY `idx_answer` (`answer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问卷-答案明细';

-- ----------------------------
-- 示例数据（演示一份“迎新信息登记”问卷）
-- ----------------------------
INSERT INTO `tb_survey` (`title`, `status`, `visible_all`, `deadline`, `remark`, `create_by`, `create_time`)
VALUES ('迎新信息登记（示例）', '1', '1', DATE_ADD(NOW(), INTERVAL 7 DAY), '最简示例：文本+单选+多选', 'admin', NOW());

-- 题目（三道题：文本/单选/多选）
INSERT INTO `tb_survey_item` (`survey_id`, `title`, `type`, `required`, `sort_no`, `create_by`, `create_time`)
SELECT s.id, '你的自我介绍', 1, '1', 0, 'admin', NOW() FROM tb_survey s WHERE s.title='迎新信息登记（示例）';
INSERT INTO `tb_survey_item` (`survey_id`, `title`, `type`, `required`, `sort_no`, `create_by`, `create_time`)
SELECT s.id, '宿舍是否分配完成？', 2, '1', 1, 'admin', NOW() FROM tb_survey s WHERE s.title='迎新信息登记（示例）';
INSERT INTO `tb_survey_item` (`survey_id`, `title`, `type`, `required`, `sort_no`, `create_by`, `create_time`)
SELECT s.id, '你感兴趣的社团（可多选）', 3, '0', 2, 'admin', NOW() FROM tb_survey s WHERE s.title='迎新信息登记（示例）';

-- 为第2题添加选项
INSERT INTO `tb_survey_option` (`item_id`, `label`, `sort_no`, `create_by`, `create_time`)
SELECT i.id, '是', 0, 'admin', NOW() FROM tb_survey_item i WHERE i.title='宿舍是否分配完成？' AND i.type=2;
INSERT INTO `tb_survey_option` (`item_id`, `label`, `sort_no`, `create_by`, `create_time`)
SELECT i.id, '否', 1, 'admin', NOW() FROM tb_survey_item i WHERE i.title='宿舍是否分配完成？' AND i.type=2;

-- 为第3题添加选项
INSERT INTO `tb_survey_option` (`item_id`, `label`, `sort_no`, `create_by`, `create_time`)
SELECT i.id, '篮球社', 0, 'admin', NOW() FROM tb_survey_item i WHERE i.title='你感兴趣的社团（可多选）' AND i.type=3;
INSERT INTO `tb_survey_option` (`item_id`, `label`, `sort_no`, `create_by`, `create_time`)
SELECT i.id, '吉他社', 1, 'admin', NOW() FROM tb_survey_item i WHERE i.title='你感兴趣的社团（可多选）' AND i.type=3;
INSERT INTO `tb_survey_option` (`item_id`, `label`, `sort_no`, `create_by`, `create_time`)
SELECT i.id, '羽毛球社', 2, 'admin', NOW() FROM tb_survey_item i WHERE i.title='你感兴趣的社团（可多选）' AND i.type=3;

/* ======================================================================
   变更记录
   日期：2025-11-07
   模块：问卷（置顶能力）
   说明：为 tb_survey 新增置顶字段 pinned(0/1) 与 pinned_time。
====================================================================== */
ALTER TABLE `tb_survey`
  ADD COLUMN `pinned`      CHAR(1)     NOT NULL DEFAULT '0' COMMENT '是否置顶：0否 1是' AFTER `remark`,
  ADD COLUMN `pinned_time` DATETIME             DEFAULT NULL COMMENT '置顶时间' AFTER `pinned`;
```