```sql
/* ======================================================================
   数字图书馆模块（结构修订 v3，最终执行版）
   日期：2025-11-03
   说明：
   - ISBN 仅存数字字符串（ISBN-13），移除展示用带连字符的 `isbn` 列。
   - 示例数据与辅助查询改为按 `isbn13` 匹配。
====================================================================== */

-- 最终命名：统一前缀 tb_library_，避免与其他模块冲突
-- 图书表：tb_library_book（仅保留 isbn13 数字唯一）
CREATE TABLE IF NOT EXISTS `tb_library_book` (
  `id`                 BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `isbn13`             CHAR(13)      NOT NULL COMMENT 'ISBN-13（仅数字，唯一）',
  `title`              VARCHAR(256)  NOT NULL COMMENT '书名',
  `author`             VARCHAR(256)  NOT NULL COMMENT '作者',
  `publisher`          VARCHAR(256)           DEFAULT NULL COMMENT '出版社',
  `publish_year`       INT                    DEFAULT NULL COMMENT '出版年份',
  `language`           VARCHAR(32)            DEFAULT NULL COMMENT '语种',
  `keywords`           VARCHAR(512)           DEFAULT NULL COMMENT '关键词（逗号分隔）',
  `summary`            TEXT                   DEFAULT NULL COMMENT '简介',
  `cover_url`          VARCHAR(512)           DEFAULT NULL COMMENT '封面URL',
  `status`             CHAR(1)       NOT NULL DEFAULT '0' COMMENT '状态（0待审 1已通过 2驳回 3已下架）',
  `audit_by`           VARCHAR(64)            DEFAULT NULL COMMENT '审核人',
  `audit_time`         DATETIME               DEFAULT NULL COMMENT '审核时间',
  `audit_reason`       VARCHAR(500)           DEFAULT NULL COMMENT '审核意见/驳回原因',
  `publish_time`       DATETIME               DEFAULT NULL COMMENT '发布时间（通过时写入）',
  `download_count`     BIGINT        NOT NULL DEFAULT 0 COMMENT '下载总次数',
  `last_download_time` DATETIME               DEFAULT NULL COMMENT '最近下载时间',
  `uploader_id`        BIGINT        NOT NULL COMMENT '上传者用户ID',
  `uploader_name`      VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '上传者姓名/账号快照',
  `create_by`          VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`        DATETIME               DEFAULT NULL COMMENT '创建时间',
  `update_by`          VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`        DATETIME               DEFAULT NULL COMMENT '更新时间',
  `del_flag`           CHAR(1)       NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_isbn13` (`isbn13`),
  KEY `idx_status_publish` (`status`,`publish_time`),
  KEY `idx_title` (`title`),
  KEY `idx_author` (`author`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数字图书馆-图书主体（v3，library）';

-- 示例数据（幂等插入）
INSERT INTO `tb_library_book` (
  `isbn13`,`title`,`author`,`publisher`,`publish_year`,`language`,`keywords`,`summary`,`cover_url`,`status`,`download_count`,`uploader_id`,`uploader_name`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`
) SELECT '9787300000011','计算机网络（第7版）','谢希仁','电子工业出版社',2017,'zh','网络,协议,教材','经典网络教材',NULL,'1',5,1001,'alice','alice',NOW(),'alice',NOW(),'0'
  WHERE NOT EXISTS (SELECT 1 FROM `tb_library_book` WHERE `isbn13`='9787300000011');
INSERT INTO `tb_library_book` (
  `isbn13`,`title`,`author`,`publisher`,`publish_year`,`language`,`keywords`,`summary`,`cover_url`,`status`,`download_count`,`uploader_id`,`uploader_name`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`
) SELECT '9787110000028','Java 核心技术 卷I','Cay S. Horstmann','机械工业出版社',2022,'zh','Java,基础,面向对象','Java 入门与进阶',NULL,'0',0,1002,'bob','bob',NOW(),'bob',NOW(),'0'
  WHERE NOT EXISTS (SELECT 1 FROM `tb_library_book` WHERE `isbn13`='9787110000028');

-- 按 isbn13 关联资产/收藏/日志的示例（如需重新灌入）
-- INSERT INTO `tb_library_book_asset` ... SELECT b.id FROM tb_library_book b WHERE b.isbn13='9787300000011';
-- INSERT INTO `tb_library_book_favorite` ... SELECT b.id FROM tb_library_book b WHERE b.isbn13='9787300000011';
-- INSERT INTO `tb_library_book_download_log` ... SELECT b.id FROM tb_library_book b WHERE b.isbn13='9787300000011' LIMIT 1;

-- ----------------------------
-- 图书资产：tb_library_book_asset（最终版）
-- 支持多格式文件或外链；硬删除时用于定位 OSS 对象
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_library_book_asset` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id`       BIGINT        NOT NULL COMMENT '图书ID（tb_library_book.id）',
  `asset_type`    CHAR(1)       NOT NULL COMMENT '资产类型（0文件 1外链）',
  `format`        VARCHAR(16)            DEFAULT NULL COMMENT '文件格式（pdf/epub/mobi/zip/other）',
  `file_url`      VARCHAR(512)           DEFAULT NULL COMMENT '文件URL（OSS/网关）',
  `file_size`     BIGINT                 DEFAULT NULL COMMENT '文件大小（字节）',
  `file_hash`     VARCHAR(128)           DEFAULT NULL COMMENT '文件哈希（SHA-256）',
  `oss_object_key` VARCHAR(512)          DEFAULT NULL COMMENT 'OSS 对象键（用于删除）',
  `link_url`      VARCHAR(512)           DEFAULT NULL COMMENT '外链URL',
  `sort`          INT            NOT NULL DEFAULT 0 COMMENT '排序（下载时的优先顺序）',
  `create_by`     VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`   DATETIME                DEFAULT NULL COMMENT '创建时间',
  `update_by`     VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`   DATETIME                DEFAULT NULL COMMENT '更新时间',
  `del_flag`      CHAR(1)        NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_book_id` (`book_id`),
  CONSTRAINT `fk_library_asset_book` FOREIGN KEY (`book_id`) REFERENCES `tb_library_book` (`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_library_book_file` (`book_id`,`asset_type`,`format`,`file_hash`),
  UNIQUE KEY `uk_library_book_link` (`book_id`,`asset_type`,`link_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数字图书馆-图书资产（多格式/外链）';

-- ----------------------------
-- 收藏关系：tb_library_book_favorite（最终版）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_library_book_favorite` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id`      BIGINT       NOT NULL COMMENT '图书ID（tb_library_book.id）',
  `user_id`      BIGINT       NOT NULL COMMENT '用户ID',
  `create_time`  DATETIME              DEFAULT NULL COMMENT '收藏时间',
  `create_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `update_by`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`  DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_library_book_user` (`book_id`,`user_id`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_library_book_fav_book` FOREIGN KEY (`book_id`) REFERENCES `tb_library_book` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数字图书馆-收藏关系';

-- ----------------------------
-- 下载日志：tb_library_book_download_log（最终版）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_library_book_download_log` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id`     BIGINT       NOT NULL COMMENT '图书ID（tb_library_book.id）',
  `asset_id`    BIGINT                DEFAULT NULL COMMENT '资产ID（可空）',
  `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
  `result`      CHAR(1)      NOT NULL DEFAULT '0' COMMENT '结果（0成功 1失败）',
  `ip`          VARCHAR(64)           DEFAULT NULL COMMENT '来源IP',
  `ua`          VARCHAR(256)          DEFAULT NULL COMMENT 'User-Agent 截断',
  `referer`     VARCHAR(256)          DEFAULT NULL COMMENT '来源页',
  `create_time` DATETIME              DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_book_time` (`book_id`,`create_time`),
  KEY `idx_user_time` (`user_id`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数字图书馆-下载日志';

-- ----------------------------
-- 操作日志：tb_library_book_log（最终版）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_library_book_log` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id`     BIGINT       NOT NULL COMMENT '图书ID（tb_library_book.id）',
  `action`      VARCHAR(32)  NOT NULL COMMENT '动作（CREATE/EDIT/SUBMIT/APPROVE/REJECT/OFFLINE/ONLINE/DOWNLOAD/DELETE/HARD_DELETE）',
  `actor_id`    BIGINT                DEFAULT NULL COMMENT '操作者ID',
  `actor_name`  VARCHAR(64)           DEFAULT NULL COMMENT '操作者名称',
  `ip`          VARCHAR(64)           DEFAULT NULL COMMENT 'IP',
  `ua`          VARCHAR(256)          DEFAULT NULL COMMENT 'UA',
  `detail`      VARCHAR(1000)         DEFAULT NULL COMMENT '详情/备注',
  `result`      VARCHAR(16)           DEFAULT NULL COMMENT '结果（SUCCESS/FAIL）',
  `create_time` DATETIME              DEFAULT NULL COMMENT '时间',
  `del_flag`    CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_book_time` (`book_id`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数字图书馆-操作日志';

-- ----------------------------
-- 图书管理员映射：tb_library_librarian（最终版）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_library_librarian` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`     BIGINT       NOT NULL COMMENT '系统用户ID',
  `username`    VARCHAR(64)           DEFAULT NULL COMMENT '账号快照',
  `nickname`    VARCHAR(64)           DEFAULT NULL COMMENT '姓名快照',
  `remark`      VARCHAR(255)          DEFAULT NULL COMMENT '备注',
  `create_by`   VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME              DEFAULT NULL COMMENT '任命时间',
  `update_by`   VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME              DEFAULT NULL COMMENT '更新时间',
  `del_flag`    CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数字图书馆-图书管理员映射（与系统角色联动）';

-- ----------------------------
-- 示例数据补充（资产/收藏/日志）
-- ----------------------------
INSERT INTO `tb_library_book_asset`(`book_id`,`asset_type`,`format`,`file_url`,`file_size`,`file_hash`,`oss_object_key`,`sort`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`)
SELECT b.id,'0','pdf','https://oss.example.com/books/networks7.pdf',10485760,'sha256:demo01','books/networks7.pdf',1,'alice',NOW(),'alice',NOW(),'0' FROM tb_library_book b WHERE b.isbn13='9787300000011' AND NOT EXISTS (
  SELECT 1 FROM tb_library_book_asset a WHERE a.book_id=b.id AND a.asset_type='0' AND a.format='pdf' AND a.file_hash='sha256:demo01'
);
INSERT INTO `tb_library_book_asset`(`book_id`,`asset_type`,`link_url`,`sort`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`)
SELECT b.id,'1','https://example.com/networks7',2,'alice',NOW(),'alice',NOW(),'0' FROM tb_library_book b WHERE b.isbn13='9787300000011' AND NOT EXISTS (
  SELECT 1 FROM tb_library_book_asset a WHERE a.book_id=b.id AND a.asset_type='1' AND a.link_url='https://example.com/networks7'
);
INSERT INTO `tb_library_book_favorite`(`book_id`,`user_id`,`create_time`,`create_by`)
SELECT b.id,1003,NOW(),'charlie' FROM tb_library_book b WHERE b.isbn13='9787300000011' AND NOT EXISTS (
  SELECT 1 FROM tb_library_book_favorite f WHERE f.book_id=b.id AND f.user_id=1003
);
INSERT INTO `tb_library_book_download_log`(`book_id`,`asset_id`,`user_id`,`result`,`ip`,`ua`,`referer`,`create_time`)
SELECT b.id,a.id,1003,'0','127.0.0.1','curl/7.79','/p/book',NOW()
FROM tb_library_book b JOIN tb_library_book_asset a ON a.book_id=b.id AND a.asset_type='0' WHERE b.isbn13='9787300000011' AND NOT EXISTS (
  SELECT 1 FROM tb_library_book_download_log dl WHERE dl.book_id=b.id AND dl.user_id=1003 AND dl.asset_id=a.id
) LIMIT 1;
INSERT INTO `tb_library_book_log`(`book_id`,`action`,`actor_id`,`actor_name`,`detail`,`result`,`create_time`)
SELECT b.id,'APPROVE',2001,'librarian01','审核通过','SUCCESS',NOW() FROM tb_library_book b WHERE b.isbn13='9787300000011' AND NOT EXISTS (
  SELECT 1 FROM tb_library_book_log l WHERE l.book_id=b.id AND l.action='APPROVE'
);
```