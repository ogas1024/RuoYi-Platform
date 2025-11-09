```sql
-- ------------------------------------------------------------
-- 2025-10-19 模块：通知公告（MVP）
-- 变更说明：新增公告主体、可见范围、附件与阅读回执四张业务表
-- 约定：不做物理外键；与 RuoYi 的 sys_user/sys_role/sys_dept/sys_post 逻辑关联
-- ------------------------------------------------------------

-- tb_notice：通知公告-主体
CREATE TABLE IF NOT EXISTS tb_notice (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  title VARCHAR(200) NOT NULL COMMENT '标题',
  content_html MEDIUMTEXT NOT NULL COMMENT '富文本HTML内容',
  type TINYINT NOT NULL DEFAULT 2 COMMENT '类型：1-通知 2-公告',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-草稿 1-已发布 2-撤回 3-已过期',
  visible_all TINYINT NOT NULL DEFAULT 1 COMMENT '是否全员可见：1-是 0-否',
  publisher_id BIGINT NOT NULL COMMENT '发布者用户ID（sys_user.user_id）',
  publish_time DATETIME NULL COMMENT '发布时间',
  expire_time DATETIME NULL COMMENT '到期时间（超过即视为已过期）',
  pinned TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
  pinned_time DATETIME NULL COMMENT '置顶时间',
  edit_count INT NOT NULL DEFAULT 0 COMMENT '编辑次数',
  read_count INT NOT NULL DEFAULT 0 COMMENT '阅读次数（冗余计数）',
  attachment_count INT NOT NULL DEFAULT 0 COMMENT '附件数量（冗余计数）',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag CHAR(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告-主体';

-- 索引
CREATE INDEX idx_notice_status_pub ON tb_notice(status, pinned, pinned_time, publish_time);
CREATE INDEX idx_notice_expire ON tb_notice(expire_time);
CREATE INDEX idx_notice_publisher ON tb_notice(publisher_id);
CREATE INDEX idx_notice_title ON tb_notice(title);

-- tb_notice_scope：通知公告-可见范围
CREATE TABLE IF NOT EXISTS tb_notice_scope (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  notice_id BIGINT NOT NULL COMMENT '公告ID（tb_notice.id）',
  scope_type TINYINT NOT NULL COMMENT '范围类型：0-角色 1-部门 2-岗位',
  ref_id BIGINT NOT NULL COMMENT '引用ID：sys_role.role_id / sys_dept.dept_id / sys_post.post_id',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  create_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag CHAR(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告-可见范围';

-- 索引与唯一约束（避免重复范围项）
CREATE INDEX idx_scope_notice ON tb_notice_scope(notice_id);
CREATE INDEX idx_scope_type_ref ON tb_notice_scope(scope_type, ref_id);
CREATE UNIQUE INDEX uk_scope_unique ON tb_notice_scope(notice_id, scope_type, ref_id, del_flag);

-- tb_notice_attachment：通知公告-附件
CREATE TABLE IF NOT EXISTS tb_notice_attachment (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  notice_id BIGINT NOT NULL COMMENT '公告ID（tb_notice.id）',
  file_name VARCHAR(255) NOT NULL COMMENT '文件名',
  file_url VARCHAR(1024) NOT NULL COMMENT '文件URL（OSS）',
  file_type VARCHAR(50) DEFAULT NULL COMMENT '文件类型（扩展名或MIME）',
  file_size BIGINT DEFAULT NULL COMMENT '文件大小（字节）',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  create_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag CHAR(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告-附件';

CREATE INDEX idx_attach_notice ON tb_notice_attachment(notice_id);

-- tb_notice_read：通知公告-阅读回执
CREATE TABLE IF NOT EXISTS tb_notice_read (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  notice_id BIGINT NOT NULL COMMENT '公告ID（tb_notice.id）',
  user_id BIGINT NOT NULL COMMENT '用户ID（sys_user.user_id）',
  ack TINYINT NOT NULL DEFAULT 0 COMMENT '是否确认（预留，MVP为0）',
  read_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  create_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) NOT NULL DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag CHAR(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告-阅读回执';

-- 索引与唯一约束（一个用户对同一公告仅保留一条有效回执）
CREATE UNIQUE INDEX uk_notice_read ON tb_notice_read(notice_id, user_id, del_flag);
CREATE INDEX idx_read_user ON tb_notice_read(user_id);

-- 示例数据（最小集）
INSERT INTO tb_notice (
  title, content_html, type, status, visible_all, publisher_id,
  publish_time, expire_time, pinned, pinned_time,
  edit_count, read_count, attachment_count, remark, create_by, update_by
) VALUES
('测试公告（全员可见）', '<p>欢迎使用通知公告模块（MVP）</p>', 2, 1, 1, 1,
 NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, NOW(),
 0, 0, 0, '示例数据', 'admin', 'admin');
 ```