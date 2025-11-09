create table ruoyi.sys_oper_log
(
    oper_id        bigint auto_increment comment '日志主键'
        primary key,
    title          varchar(50)   default '' null comment '模块标题',
    business_type  int           default 0  null comment '业务类型（0其它 1新增 2修改 3删除）',
    method         varchar(200)  default '' null comment '方法名称',
    request_method varchar(10)   default '' null comment '请求方式',
    operator_type  int           default 0  null comment '操作类别（0其它 1后台用户 2手机端用户）',
    oper_name      varchar(50)   default '' null comment '操作人员',
    dept_name      varchar(50)   default '' null comment '部门名称',
    oper_url       varchar(255)  default '' null comment '请求URL',
    oper_ip        varchar(128)  default '' null comment '主机地址',
    oper_location  varchar(255)  default '' null comment '操作地点',
    oper_param     varchar(2000) default '' null comment '请求参数',
    json_result    varchar(2000) default '' null comment '返回参数',
    status         int           default 0  null comment '操作状态（0正常 1异常）',
    error_msg      varchar(2000) default '' null comment '错误消息',
    oper_time      datetime                 null comment '操作时间',
    cost_time      bigint        default 0  null comment '消耗时间'
)
    comment '操作日志记录' collate = utf8mb4_general_ci
                           row_format = DYNAMIC;

create index idx_sys_oper_log_bt
    on ruoyi.sys_oper_log (business_type);

create index idx_sys_oper_log_ot
    on ruoyi.sys_oper_log (oper_time);

create index idx_sys_oper_log_s
    on ruoyi.sys_oper_log (status);

create table ruoyi.sys_post
(
    post_id     bigint auto_increment comment '岗位ID'
        primary key,
    post_code   varchar(64)            not null comment '岗位编码',
    post_name   varchar(50)            not null comment '岗位名称',
    post_sort   int                    not null comment '显示顺序',
    status      char                   not null comment '状态（0正常 1停用）',
    create_by   varchar(64) default '' null comment '创建者',
    create_time datetime               null comment '创建时间',
    update_by   varchar(64) default '' null comment '更新者',
    update_time datetime               null comment '更新时间',
    remark      varchar(500)           null comment '备注'
)
    comment '岗位信息表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

create table ruoyi.sys_role
(
    role_id             bigint auto_increment comment '角色ID'
        primary key,
    role_name           varchar(30)             not null comment '角色名称',
    role_key            varchar(100)            not null comment '角色权限字符串',
    role_sort           int                     not null comment '显示顺序',
    data_scope          char        default '1' null comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    menu_check_strictly tinyint(1)  default 1   null comment '菜单树选择项是否关联显示',
    dept_check_strictly tinyint(1)  default 1   null comment '部门树选择项是否关联显示',
    status              char                    not null comment '角色状态（0正常 1停用）',
    del_flag            char        default '0' null comment '删除标志（0代表存在 2代表删除）',
    create_by           varchar(64) default ''  null comment '创建者',
    create_time         datetime                null comment '创建时间',
    update_by           varchar(64) default ''  null comment '更新者',
    update_time         datetime                null comment '更新时间',
    remark              varchar(500)            null comment '备注'
)
    comment '角色信息表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

create table ruoyi.sys_role_dept
(
    role_id bigint not null comment '角色ID',
    dept_id bigint not null comment '部门ID',
    primary key (role_id, dept_id)
)
    comment '角色和部门关联表' collate = utf8mb4_general_ci
                               row_format = DYNAMIC;

create table ruoyi.sys_role_menu
(
    role_id bigint not null comment '角色ID',
    menu_id bigint not null comment '菜单ID',
    primary key (role_id, menu_id)
)
    comment '角色和菜单关联表' collate = utf8mb4_general_ci
                               row_format = DYNAMIC;

create table ruoyi.sys_user
(
    user_id         bigint auto_increment comment '用户ID'
        primary key,
    dept_id         bigint                    null comment '部门ID',
    user_name       varchar(30)               not null comment '用户账号',
    nick_name       varchar(30)               not null comment '用户昵称',
    user_type       varchar(2)   default '00' null comment '用户类型（00系统用户）',
    email           varchar(50)  default ''   null comment '用户邮箱',
    phonenumber     varchar(11)  default ''   null comment '手机号码',
    sex             char         default '0'  null comment '用户性别（0男 1女 2未知）',
    avatar          varchar(100) default ''   null comment '头像地址',
    password        varchar(100) default ''   null comment '密码',
    status          char         default '0'  null comment '账号状态（0正常 1停用）',
    del_flag        char         default '0'  null comment '删除标志（0代表存在 2代表删除）',
    login_ip        varchar(128) default ''   null comment '最后登录IP',
    login_date      datetime                  null comment '最后登录时间',
    pwd_update_date datetime                  null comment '密码最后更新时间',
    create_by       varchar(64)  default ''   null comment '创建者',
    create_time     datetime                  null comment '创建时间',
    update_by       varchar(64)  default ''   null comment '更新者',
    update_time     datetime                  null comment '更新时间',
    remark          varchar(500)              null comment '备注'
)
    comment '用户信息表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

create table ruoyi.sys_user_post
(
    user_id bigint not null comment '用户ID',
    post_id bigint not null comment '岗位ID',
    primary key (user_id, post_id)
)
    comment '用户与岗位关联表' collate = utf8mb4_general_ci
                               row_format = DYNAMIC;

create table ruoyi.sys_user_role
(
    user_id bigint not null comment '用户ID',
    role_id bigint not null comment '角色ID',
    primary key (user_id, role_id)
)
    comment '用户和角色关联表' collate = utf8mb4_general_ci
                               row_format = DYNAMIC;

create table ruoyi.tb_book
(
    id                 bigint auto_increment comment '主键ID'
        primary key,
    isbn13             char(13)                not null comment 'ISBN-13（仅数字，唯一）',
    title              varchar(256)            not null comment '书名',
    author             varchar(256)            not null comment '作者',
    publisher          varchar(256)            null comment '出版社',
    publish_year       int                     null comment '出版年份',
    language           varchar(32)             null comment '语种',
    keywords           varchar(512)            null comment '关键词（逗号分隔）',
    summary            text                    null comment '简介',
    cover_url          varchar(512)            null comment '封面URL',
    status             char        default '0' not null comment '状态（0待审 1已通过 2驳回 3已下架）',
    audit_by           varchar(64)             null comment '审核人',
    audit_time         datetime                null comment '审核时间',
    audit_reason       varchar(500)            null comment '审核意见/驳回原因',
    publish_time       datetime                null comment '发布时间（通过时写入）',
    download_count     bigint      default 0   not null comment '下载总次数',
    last_download_time datetime                null comment '最近下载时间',
    uploader_id        bigint                  not null comment '上传者用户ID',
    uploader_name      varchar(64) default ''  not null comment '上传者姓名/账号快照',
    create_by          varchar(64) default ''  not null comment '创建者',
    create_time        datetime                null comment '创建时间',
    update_by          varchar(64) default ''  not null comment '更新者',
    update_time        datetime                null comment '更新时间',
    del_flag           char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_isbn13
        unique (isbn13)
)
    comment '数字图书馆-图书主体（v3）' charset = utf8mb4;

create index idx_author
    on ruoyi.tb_book (author);

create index idx_status_publish
    on ruoyi.tb_book (status, publish_time);

create index idx_title
    on ruoyi.tb_book (title);

create table ruoyi.tb_carts
(
    id           int auto_increment comment '主键'
        primary key,
    user_id      int                                 not null comment '用户ID',
    book_id      int                                 not null comment '图书ID',
    quantity     int       default 1                 not null comment '购买数量',
    created_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '购物车表' engine = MyISAM
                       collate = utf8mb4_general_ci
                       row_format = FIXED;

create table ruoyi.tb_category
(
    id            int auto_increment comment '类别ID'
        primary key,
    category_name varchar(100) null comment '类别名称'
)
    comment '图书类别表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

create table ruoyi.tb_course
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    major_id    bigint                  not null comment '所属专业ID',
    course_name varchar(255)            not null comment '课程名称',
    course_code varchar(64)             null comment '课程编码（可选）',
    status      char        default '0' not null comment '状态（0正常 1停用）',
    remark      varchar(500)            null comment '备注',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_course_unique
        unique (major_id, course_name)
)
    comment '课程表' charset = utf8mb4;

create index idx_course_major
    on ruoyi.tb_course (major_id);

create table ruoyi.tb_course_resource
(
    id                 bigint auto_increment comment '主键ID'
        primary key,
    major_id           bigint                  not null comment '专业ID（冗余便于索引）',
    course_id          bigint                  not null comment '课程ID',
    resource_name      varchar(255)            not null comment '资源名称（用户自定义）',
    resource_type      tinyint                 not null comment '资源类型：0-文件 1-外链',
    file_url           varchar(512)            null comment '文件直链（OSS，resource_type=0时必填）',
    file_hash          varchar(128)            null comment '文件哈希（SHA-256，resource_type=0时必填）',
    file_size          bigint                  null comment '文件大小（字节）',
    link_url           varchar(512)            null comment '外链URL（resource_type=1时必填）',
    description        text                    not null comment '资源简介（必填）',
    status             tinyint     default 0   not null comment '状态：0待审 1已通过 2驳回 3已下架',
    audit_by           varchar(64)             null comment '审核人',
    audit_time         datetime                null comment '审核时间',
    audit_reason       varchar(512)            null comment '驳回理由',
    publish_time       datetime                null comment '首次上架时间',
    download_count     int         default 0   not null comment '下载次数',
    last_download_time datetime                null comment '最近下载时间',
    uploader_id        bigint                  not null comment '上传者用户ID',
    uploader_name      varchar(64)             not null comment '上传者名称（冗余）',
    create_by          varchar(64) default ''  not null comment '创建者',
    create_time        datetime                null comment '创建时间',
    update_by          varchar(64) default ''  not null comment '更新者',
    update_time        datetime                null comment '更新时间',
    del_flag           char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_course_filehash
        unique (course_id, resource_type, file_hash),
    constraint uk_course_linkurl
        unique (course_id, resource_type, link_url(191))
)
    comment '课程资源表（仅压缩包或外链）' charset = utf8mb4;

create index idx_course_status
    on ruoyi.tb_course_resource (course_id, status);

create index idx_download_count
    on ruoyi.tb_course_resource (download_count);

create index idx_major_course
    on ruoyi.tb_course_resource (major_id, course_id);

create index idx_status_time
    on ruoyi.tb_course_resource (status, create_time);

create index idx_uploader_id
    on ruoyi.tb_course_resource (uploader_id);

create table ruoyi.tb_course_resource_best
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    resource_id bigint                  not null comment '资源ID（唯一）',
    best_by     varchar(64)             not null comment '最佳标记人',
    best_time   datetime                not null comment '最佳标记时间',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_best_resource
        unique (resource_id)
)
    comment '课程资源最佳标记表' charset = utf8mb4;

create table ruoyi.tb_course_resource_log
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    resource_id bigint                  not null comment '资源ID',
    action      varchar(32)             not null comment 'CREATE/EDIT/APPROVE/REJECT/OFFLINE/ONLINE/DOWNLOAD/DELETE/HARD_DELETE',
    actor_id    bigint                  null comment '操作者ID',
    actor_name  varchar(64)             null comment '操作者名称',
    ip          varchar(64)             null comment 'IP地址',
    user_agent  varchar(255)            null comment 'User-Agent',
    detail      text                    null comment '详情（JSON文本）',
    result      varchar(16)             null comment 'SUCCESS/FAIL',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）'
)
    comment '课程资源操作日志表（审计）' charset = utf8mb4;

create index idx_log_resource_time
    on ruoyi.tb_course_resource_log (resource_id, create_time);

create table ruoyi.tb_cr_user_score
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    user_id       bigint                  not null comment '用户ID（sys_user.id）',
    username      varchar(64) default ''  not null comment '用户名快照（冗余）',
    major_id      bigint      default 0   not null comment '专业ID；0=全站',
    total_score   int         default 0   not null comment '累计积分',
    approve_count int         default 0   not null comment '审核通过次数（仅第一次通过计数）',
    best_count    int         default 0   not null comment '被评为最佳次数（仅第一次计数）',
    remark        varchar(255)            null comment '备注',
    create_by     varchar(64) default ''  not null comment '创建者',
    create_time   datetime                null comment '创建时间',
    update_by     varchar(64) default ''  not null comment '更新者',
    update_time   datetime                null comment '更新时间',
    del_flag      char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_user_major
        unique (user_id, major_id)
)
    comment '课程资源（CR）用户积分聚合表（用户×专业；0=全站）' charset = utf8mb4;

create index idx_major_score
    on ruoyi.tb_cr_user_score (major_id, total_score);

create index idx_user
    on ruoyi.tb_cr_user_score (user_id);

create table ruoyi.tb_cr_user_score_log
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    user_id     bigint                  not null comment '得分用户ID（资源上传者）',
    username    varchar(64) default ''  not null comment '用户名快照（冗余）',
    major_id    bigint      default 0   not null comment '资源所属专业ID；0=全站',
    resource_id bigint                  not null comment '资源ID（tb_course_resource.id）',
    event_type  varchar(16)             not null comment 'APPROVE/BEST',
    delta       int                     not null comment '积分变动（正数）',
    remark      varchar(255)            null comment '备注',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_once_event
        unique (user_id, resource_id, event_type)
)
    comment '课程资源（CR）用户积分变动流水（一次性发放）' charset = utf8mb4;

create index idx_major_time
    on ruoyi.tb_cr_user_score_log (major_id, create_time);

create index idx_user_time
    on ruoyi.tb_cr_user_score_log (user_id, create_time);

create table ruoyi.tb_facility_ban
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    user_id     bigint                  not null comment '用户ID（申请人）',
    reason      varchar(500)            not null comment '封禁原因',
    status      char        default '0' not null comment '状态（0生效 1失效）',
    expire_time datetime                null comment '到期时间（可选）',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_ban_user_status
        unique (user_id, status)
)
    comment '申请人封禁表' charset = utf8mb4;

create index idx_ban_user
    on ruoyi.tb_facility_ban (user_id);

create table ruoyi.tb_facility_booking
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    room_id          bigint                  not null comment '功能房ID',
    applicant_id     bigint                  not null comment '预约申请人用户ID',
    purpose          varchar(200)            not null comment '使用目的（1~200字）',
    start_time       datetime                not null comment '开始时间（30分钟粒度）',
    end_time         datetime                not null comment '结束时间（>开始时间，≤全局上限）',
    duration_minutes int                     not null comment '预约时长（分钟）',
    status           char        default '0' not null comment '状态（0待审 1已批准 2已驳回 3已取消 4进行中 5已完成）',
    reject_reason    varchar(500)            null comment '驳回理由（必填于驳回）',
    create_by        varchar(64) default ''  not null comment '创建者',
    create_time      datetime                null comment '创建时间',
    update_by        varchar(64) default ''  not null comment '更新者',
    update_time      datetime                null comment '更新时间',
    del_flag         char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_booking_once_status
        unique (room_id, applicant_id, start_time, end_time, status)
)
    comment '功能房预约表' charset = utf8mb4;

create index idx_booking_applicant_time
    on ruoyi.tb_facility_booking (applicant_id, start_time);

create index idx_booking_room_time
    on ruoyi.tb_facility_booking (room_id, start_time, end_time);

create index idx_booking_status
    on ruoyi.tb_facility_booking (status);

create table ruoyi.tb_facility_booking_user
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
    on ruoyi.tb_facility_booking_user (user_id);

create table ruoyi.tb_facility_building
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

create table ruoyi.tb_facility_room
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
    on ruoyi.tb_facility_room (building_id, floor_no);

create index idx_room_status
    on ruoyi.tb_facility_room (status);

create table ruoyi.tb_facility_setting
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

create table ruoyi.tb_library_book
(
    id                 bigint auto_increment comment '主键ID'
        primary key,
    isbn13             char(13)                not null comment 'ISBN-13（仅数字，唯一）',
    title              varchar(256)            not null comment '书名',
    author             varchar(256)            not null comment '作者',
    publisher          varchar(256)            null comment '出版社',
    publish_year       int                     null comment '出版年份',
    language           varchar(32)             null comment '语种',
    keywords           varchar(512)            null comment '关键词（逗号分隔）',
    summary            text                    null comment '简介',
    cover_url          varchar(512)            null comment '封面URL',
    status             char        default '0' not null comment '状态（0待审 1已通过 2驳回 3已下架）',
    audit_by           varchar(64)             null comment '审核人',
    audit_time         datetime                null comment '审核时间',
    audit_reason       varchar(500)            null comment '审核意见/驳回原因',
    publish_time       datetime                null comment '发布时间（通过时写入）',
    download_count     bigint      default 0   not null comment '下载总次数',
    last_download_time datetime                null comment '最近下载时间',
    uploader_id        bigint                  not null comment '上传者用户ID',
    uploader_name      varchar(64) default ''  not null comment '上传者姓名/账号快照',
    create_by          varchar(64) default ''  not null comment '创建者',
    create_time        datetime                null comment '创建时间',
    update_by          varchar(64) default ''  not null comment '更新者',
    update_time        datetime                null comment '更新时间',
    del_flag           char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_isbn13
        unique (isbn13)
)
    comment '数字图书馆-图书主体（v3，library）' charset = utf8mb4;

create index idx_author
    on ruoyi.tb_library_book (author);

create index idx_status_publish
    on ruoyi.tb_library_book (status, publish_time);

create index idx_title
    on ruoyi.tb_library_book (title);

create table ruoyi.tb_library_book_asset
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    book_id        bigint                  not null comment '图书ID（tb_library_book.id）',
    asset_type     char                    not null comment '资产类型（0文件 1外链）',
    format         varchar(16)             null comment '文件格式（pdf/epub/mobi/zip/other）',
    file_url       varchar(512)            null comment '文件URL（OSS/网关）',
    file_size      bigint                  null comment '文件大小（字节）',
    file_hash      varchar(128)            null comment '文件哈希（SHA-256）',
    oss_object_key varchar(512)            null comment 'OSS 对象键（用于删除）',
    link_url       varchar(512)            null comment '外链URL',
    sort           int         default 0   not null comment '排序（下载时的优先顺序）',
    create_by      varchar(64) default ''  not null comment '创建者',
    create_time    datetime                null comment '创建时间',
    update_by      varchar(64) default ''  not null comment '更新者',
    update_time    datetime                null comment '更新时间',
    del_flag       char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_library_book_file
        unique (book_id, asset_type, format, file_hash),
    constraint uk_library_book_link
        unique (book_id, asset_type, link_url),
    constraint fk_library_asset_book
        foreign key (book_id) references ruoyi.tb_library_book (id)
            on delete cascade
)
    comment '数字图书馆-图书资产（多格式/外链）' charset = utf8mb4;

create index idx_book_id
    on ruoyi.tb_library_book_asset (book_id);

create table ruoyi.tb_library_book_download_log
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    book_id     bigint           not null comment '图书ID（tb_library_book.id）',
    asset_id    bigint           null comment '资产ID（可空）',
    user_id     bigint           not null comment '用户ID',
    result      char default '0' not null comment '结果（0成功 1失败）',
    ip          varchar(64)      null comment '来源IP',
    ua          varchar(256)     null comment 'User-Agent 截断',
    referer     varchar(256)     null comment '来源页',
    create_time datetime         null comment '时间'
)
    comment '数字图书馆-下载日志' charset = utf8mb4;

create index idx_book_time
    on ruoyi.tb_library_book_download_log (book_id, create_time);

create index idx_user_time
    on ruoyi.tb_library_book_download_log (user_id, create_time);

create table ruoyi.tb_library_book_favorite
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    book_id     bigint                  not null comment '图书ID（tb_library_book.id）',
    user_id     bigint                  not null comment '用户ID',
    create_time datetime                null comment '收藏时间',
    create_by   varchar(64) default ''  not null comment '创建者',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_library_book_user
        unique (book_id, user_id),
    constraint fk_library_book_fav_book
        foreign key (book_id) references ruoyi.tb_library_book (id)
            on delete cascade
)
    comment '数字图书馆-收藏关系' charset = utf8mb4;

create index idx_user
    on ruoyi.tb_library_book_favorite (user_id);

create table ruoyi.tb_library_book_log
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    book_id     bigint           not null comment '图书ID（tb_library_book.id）',
    action      varchar(32)      not null comment '动作（CREATE/EDIT/SUBMIT/APPROVE/REJECT/OFFLINE/ONLINE/DOWNLOAD/DELETE/HARD_DELETE）',
    actor_id    bigint           null comment '操作者ID',
    actor_name  varchar(64)      null comment '操作者名称',
    ip          varchar(64)      null comment 'IP',
    ua          varchar(256)     null comment 'UA',
    detail      varchar(1000)    null comment '详情/备注',
    result      varchar(16)      null comment '结果（SUCCESS/FAIL）',
    create_time datetime         null comment '时间',
    del_flag    char default '0' not null comment '删除标志'
)
    comment '数字图书馆-操作日志' charset = utf8mb4;

create index idx_book_time
    on ruoyi.tb_library_book_log (book_id, create_time);

create table ruoyi.tb_library_librarian
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    user_id     bigint                  not null comment '系统用户ID',
    username    varchar(64)             null comment '账号快照',
    nickname    varchar(64)             null comment '姓名快照',
    remark      varchar(255)            null comment '备注',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '任命时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_user
        unique (user_id)
)
    comment '数字图书馆-图书管理员映射（与系统角色联动）' charset = utf8mb4;

create table ruoyi.tb_lost_item
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    type           varchar(10)             not null comment '类型：lost=丢失 found=捡到',
    title          varchar(100)            not null comment '标题（2~50 字）',
    content        text                    not null comment '正文描述（5~2000 字）',
    location       varchar(100)            null comment '地点（可选）',
    lost_time      datetime                null comment '发生时间（可选）',
    views          int         default 0   not null comment '浏览量',
    status         char        default '1' not null comment '状态：0草稿 1待审 2已发 3驳回 4下架',
    solved_flag    char        default '0' not null comment '是否已解决：0否 1是（不可回退）',
    publish_time   datetime                null comment '发布时间（通过审核时写入）',
    reject_reason  varchar(500)            null comment '驳回原因（必填于驳回动作）',
    offline_reason varchar(500)            null comment '下架原因（必填于下架动作）',
    contact_info   varchar(50)             null comment '联系信息（自由文本，例如电话/微信等，≤50字）',
    create_by      varchar(64) default ''  not null comment '创建者',
    create_time    datetime                null comment '创建时间',
    update_by      varchar(64) default ''  not null comment '更新者',
    update_time    datetime                null comment '更新时间',
    del_flag       char        default '0' not null comment '删除标志（0存在 2删除）'
)
    comment '失物招领-主体' charset = utf8mb4;

create index idx_creator
    on ruoyi.tb_lost_item (create_by);

create index idx_status_pub
    on ruoyi.tb_lost_item (status, publish_time);

create index idx_title
    on ruoyi.tb_lost_item (title);

create index idx_type_solved
    on ruoyi.tb_lost_item (type, solved_flag);

create table ruoyi.tb_lost_item_image
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    item_id     bigint                  not null comment '条目ID（tb_lost_item.id）',
    url         varchar(500)            not null comment '图片URL（OSS）',
    sort_no     int         default 0   not null comment '排序号（0开始）',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）'
)
    comment '失物招领-图片' charset = utf8mb4;

create index idx_item_sort
    on ruoyi.tb_lost_item_image (item_id, sort_no);

create table ruoyi.tb_major
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    major_name  varchar(128)            not null comment '专业名称（唯一）',
    status      char        default '0' not null comment '状态（0正常 1停用）',
    remark      varchar(500)            null comment '备注',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_major_name
        unique (major_name)
)
    comment '专业表（独立）' charset = utf8mb4;

create table ruoyi.tb_major_lead
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    major_id    bigint                  not null comment '专业ID',
    user_id     bigint                  not null comment '负责人用户ID',
    remark      varchar(500)            null comment '备注',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_major_user
        unique (major_id, user_id)
)
    comment '专业负责人映射表' charset = utf8mb4;

create index idx_lead_user
    on ruoyi.tb_major_lead (user_id);

create table ruoyi.tb_notice
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    title            varchar(200)                          not null comment '标题',
    content_html     mediumtext                            not null comment '富文本HTML内容',
    type             tinyint     default 2                 not null comment '类型：1-通知 2-公告',
    status           tinyint     default 0                 not null comment '状态：0-草稿 1-已发布 2-撤回 3-已过期',
    visible_all      tinyint     default 1                 not null comment '是否全员可见：1-是 0-否',
    publisher_id     bigint                                not null comment '发布者用户ID（sys_user.user_id）',
    publish_time     datetime                              null comment '发布时间',
    expire_time      datetime                              null comment '到期时间（超过即视为已过期）',
    pinned           tinyint     default 0                 not null comment '是否置顶：0-否 1-是',
    pinned_time      datetime                              null comment '置顶时间',
    edit_count       int         default 0                 not null comment '编辑次数',
    read_count       int         default 0                 not null comment '阅读次数（冗余计数）',
    attachment_count int         default 0                 not null comment '附件数量（冗余计数）',
    remark           varchar(500)                          null comment '备注',
    create_by        varchar(64) default ''                not null comment '创建者',
    create_time      datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by        varchar(64) default ''                not null comment '更新者',
    update_time      datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag         char        default '0'               not null comment '删除标志（0代表存在 2代表删除）'
)
    comment '通知公告-主体' charset = utf8mb4;

create index idx_notice_expire
    on ruoyi.tb_notice (expire_time);

create index idx_notice_publisher
    on ruoyi.tb_notice (publisher_id);

create index idx_notice_status_pub
    on ruoyi.tb_notice (status, pinned, pinned_time, publish_time);

create index idx_notice_title
    on ruoyi.tb_notice (title);

create table ruoyi.tb_notice_attachment
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    notice_id   bigint                                not null comment '公告ID（tb_notice.id）',
    file_name   varchar(255)                          not null comment '文件名',
    file_url    varchar(1024)                         not null comment '文件URL（OSS）',
    file_type   varchar(50)                           null comment '文件类型（扩展名或MIME）',
    file_size   bigint                                null comment '文件大小（字节）',
    sort        int         default 0                 not null comment '排序',
    remark      varchar(255)                          null comment '备注',
    create_by   varchar(64) default ''                not null comment '创建者',
    create_time datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   varchar(64) default ''                not null comment '更新者',
    update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag    char        default '0'               not null comment '删除标志（0存在 2删除）'
)
    comment '通知公告-附件' charset = utf8mb4;

create index idx_attach_notice
    on ruoyi.tb_notice_attachment (notice_id);

create table ruoyi.tb_notice_read
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    notice_id   bigint                                not null comment '公告ID（tb_notice.id）',
    user_id     bigint                                not null comment '用户ID（sys_user.user_id）',
    ack         tinyint     default 0                 not null comment '是否确认（预留，MVP为0）',
    read_time   datetime    default CURRENT_TIMESTAMP null comment '阅读时间',
    remark      varchar(255)                          null comment '备注',
    create_by   varchar(64) default ''                not null comment '创建者',
    create_time datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   varchar(64) default ''                not null comment '更新者',
    update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag    char        default '0'               not null comment '删除标志（0存在 2删除）',
    constraint uk_notice_read
        unique (notice_id, user_id, del_flag)
)
    comment '通知公告-阅读回执' charset = utf8mb4;

create index idx_read_user
    on ruoyi.tb_notice_read (user_id);

create table ruoyi.tb_notice_scope
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    notice_id   bigint                                not null comment '公告ID（tb_notice.id）',
    scope_type  tinyint                               not null comment '范围类型：0-角色 1-部门 2-岗位',
    ref_id      bigint                                not null comment '引用ID：sys_role.role_id / sys_dept.dept_id / sys_post.post_id',
    remark      varchar(255)                          null comment '备注',
    create_by   varchar(64) default ''                not null comment '创建者',
    create_time datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   varchar(64) default ''                not null comment '更新者',
    update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    del_flag    char        default '0'               not null comment '删除标志（0存在 2删除）',
    constraint uk_scope_unique
        unique (notice_id, scope_type, ref_id, del_flag)
)
    comment '通知公告-可见范围' charset = utf8mb4;

create index idx_scope_notice
    on ruoyi.tb_notice_scope (notice_id);

create index idx_scope_type_ref
    on ruoyi.tb_notice_scope (scope_type, ref_id);

create table ruoyi.tb_survey
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    title       varchar(200)            not null comment '问卷标题/表单名称',
    status      char        default '1' not null comment '状态：0草稿 1发布 2归档',
    visible_all char        default '1' not null comment '是否全员可见：1是 0否（自定义范围）',
    deadline    datetime                null comment '截止时间（截止后不可提交或修改）',
    remark      varchar(500)            null comment '备注',
    pinned      char        default '0' not null comment '是否置顶：0否 1是',
    pinned_time datetime                null comment '置顶时间',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）'
)
    comment '问卷-主表' charset = utf8mb4;

create index idx_create_by
    on ruoyi.tb_survey (create_by);

create index idx_status_deadline
    on ruoyi.tb_survey (status, deadline);

create table ruoyi.tb_survey_answer
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    survey_id    bigint                  not null comment '问卷ID（tb_survey.id）',
    user_id      bigint                  not null comment '提交人用户ID（若依用户）',
    submit_time  datetime                null comment '提交时间',
    update_time2 datetime                null comment '最近修改时间（命名避开审计字段）',
    create_by    varchar(64) default ''  not null comment '创建者',
    create_time  datetime                null comment '创建时间',
    update_by    varchar(64) default ''  not null comment '更新者',
    update_time  datetime                null comment '更新时间',
    del_flag     char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_survey_user
        unique (survey_id, user_id)
)
    comment '问卷-答卷（每人一份）' charset = utf8mb4;

create index idx_survey
    on ruoyi.tb_survey_answer (survey_id);

create table ruoyi.tb_survey_answer_item
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    answer_id        bigint                  not null comment '答卷ID（tb_survey_answer.id）',
    item_id          bigint                  not null comment '题目ID（tb_survey_item.id）',
    value_text       text                    null comment '文本题答案',
    value_option_ids json                    null comment '单/多选答案（JSON数组，元素为 tb_survey_option.id）',
    create_by        varchar(64) default ''  not null comment '创建者',
    create_time      datetime                null comment '创建时间',
    update_by        varchar(64) default ''  not null comment '更新者',
    update_time      datetime                null comment '更新时间',
    del_flag         char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_answer_item
        unique (answer_id, item_id)
)
    comment '问卷-答案明细' charset = utf8mb4;

create index idx_answer
    on ruoyi.tb_survey_answer_item (answer_id);

create table ruoyi.tb_survey_item
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    survey_id   bigint                  not null comment '问卷ID（tb_survey.id）',
    title       varchar(300)            not null comment '题目/提示',
    type        tinyint                 not null comment '类型：1文本 2单选 3多选（预留4文件 5时间）',
    required    char        default '0' not null comment '是否必填：0否 1是',
    sort_no     int         default 0   not null comment '排序号（0开始）',
    ext_json    json                    null comment '扩展配置（占位，文件/时间等）',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）'
)
    comment '问卷-题目' charset = utf8mb4;

create index idx_survey_sort
    on ruoyi.tb_survey_item (survey_id, sort_no);

create table ruoyi.tb_survey_option
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    item_id     bigint                  not null comment '题目ID（tb_survey_item.id）',
    label       varchar(200)            not null comment '选项展示文本',
    value       varchar(200)            null comment '选项值（可空，默认=label）',
    sort_no     int         default 0   not null comment '排序号',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）'
)
    comment '问卷-选项' charset = utf8mb4;

create index idx_item_sort
    on ruoyi.tb_survey_option (item_id, sort_no);

create table ruoyi.tb_survey_scope
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    survey_id   bigint                  not null comment '问卷ID（tb_survey.id）',
    scope_type  tinyint                 not null comment '范围类型：0角色 1部门 2岗位',
    ref_id      bigint                  not null comment '引用ID：sys_role.role_id / sys_dept.dept_id / sys_post.post_id',
    create_by   varchar(64) default ''  not null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  not null comment '更新者',
    update_time datetime                null comment '更新时间',
    del_flag    char        default '0' not null comment '删除标志（0存在 2删除）',
    constraint uk_scope
        unique (survey_id, scope_type, ref_id)
)
    comment '问卷-可见范围' charset = utf8mb4;

create index idx_survey
    on ruoyi.tb_survey_scope (survey_id);

