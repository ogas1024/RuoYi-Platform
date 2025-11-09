```sql
/* ======================================================================
   失物招领模块（草案 / MVP）
   日期：2025-11-06
   说明：
   - 场景覆盖：捡到（found）与丢失（lost）两类信息统一建模，使用 type 字段区分。
   - 审核流：新增/编辑一律进入“待审核”（status=1），管理员审核通过后“已发布”（status=2）。
   - 回收站：驳回（status=3）与下架（status=4）统一归入回收站视图；可删除或“恢复为待审”。
   - 解决标记：solved_flag=1 表示作者已标记“已解决”，不可回退；默认列表隐藏，允许筛选显示。
   - 图片：最多 9 张，应用层限制格式与大小（jpg/png/webp，≤2MB/张）。
   - 管理动作：下架必须填写 offline_reason；驳回必须填写 reject_reason；恢复进入待审。
   - 审计：统一保留 create_by/create_time/update_by/update_time/del_flag。
   - 变更（2025-11-06）：联系方式字段合并为单字段 contact_info，移除 contact_name/phone/wechat。
====================================================================== */

-- ----------------------------
-- 主体：tb_lost_item
-- 关键点：
--  - type：'lost'（丢失）、'found'（捡到）
--  - status：0-草稿 1-待审核 2-已发布 3-驳回 4-已下架
--  - solved_flag：作者标记已解决（1），不可回退；与审核/上下架解耦
-- ----------------------------
DROP TABLE IF EXISTS `tb_lost_item`;
CREATE TABLE `tb_lost_item` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type`           VARCHAR(10)   NOT NULL COMMENT '类型：lost=丢失 found=捡到',
  `title`          VARCHAR(100)  NOT NULL COMMENT '标题（2~50 字）',
  `content`        TEXT          NOT NULL COMMENT '正文描述（5~2000 字）',
  `location`       VARCHAR(100)           DEFAULT NULL COMMENT '地点（可选）',
  `lost_time`      DATETIME               DEFAULT NULL COMMENT '发生时间（可选）',
  `views`          INT            NOT NULL DEFAULT 0 COMMENT '浏览量',
  `status`         CHAR(1)        NOT NULL DEFAULT '1' COMMENT '状态：0草稿 1待审 2已发 3驳回 4下架',
  `solved_flag`    CHAR(1)        NOT NULL DEFAULT '0' COMMENT '是否已解决：0否 1是（不可回退）',
  `publish_time`   DATETIME               DEFAULT NULL COMMENT '发布时间（通过审核时写入）',
  `reject_reason`  VARCHAR(500)           DEFAULT NULL COMMENT '驳回原因（必填于驳回动作）',
  `offline_reason` VARCHAR(500)           DEFAULT NULL COMMENT '下架原因（必填于下架动作）',
  `contact_info`   VARCHAR(50)            DEFAULT NULL COMMENT '联系信息（自由文本，例如电话/微信等，≤50字）',
  `create_by`      VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`    DATETIME                DEFAULT NULL COMMENT '创建时间',
  `update_by`      VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`    DATETIME                DEFAULT NULL COMMENT '更新时间',
  `del_flag`       CHAR(1)        NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_status_pub` (`status`, `publish_time`),
  KEY `idx_type_solved` (`type`, `solved_flag`),
  KEY `idx_title` (`title`),
  KEY `idx_creator` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='失物招领-主体';

-- ----------------------------
-- 图片：tb_lost_item_image（最多 9 张/条目，应用层约束）
-- ----------------------------
DROP TABLE IF EXISTS `tb_lost_item_image`;
CREATE TABLE `tb_lost_item_image` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `item_id`    BIGINT       NOT NULL COMMENT '条目ID（tb_lost_item.id）',
  `url`        VARCHAR(500) NOT NULL COMMENT '图片URL（OSS）',
  `sort_no`    INT          NOT NULL DEFAULT 0 COMMENT '排序号（0开始）',
  `create_by`  VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`DATETIME              DEFAULT NULL COMMENT '创建时间',
  `update_by`  VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`   CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_item_sort` (`item_id`, `sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='失物招领-图片';

-- ----------------------------
-- 示例数据（可按需调整/清空）
-- ----------------------------
INSERT INTO `tb_lost_item`
(`type`,`title`,`content`,`location`,`lost_time`,`status`,`solved_flag`,`publish_time`,`create_by`)
VALUES
('found','拾到校园卡一张','操场附近捡到校园卡，联系我取回。电话：188****0001','操场','2025-11-05 18:30:00','2','0','2025-11-06 09:00:00','admin'),
('lost','丢失黑色雨伞','教学楼一层遗失黑色折叠伞，若捡到请联系。微信：lost_user','教学楼一层','2025-11-04 08:00:00','2','1','2025-11-06 10:00:00','userA');

INSERT INTO `tb_lost_item_image` (`item_id`,`url`,`sort_no`) VALUES
(1,'https://oss-example.com/lostfound/card-1.jpg',0),
(2,'https://oss-example.com/lostfound/umbrella-1.jpg',0);
```