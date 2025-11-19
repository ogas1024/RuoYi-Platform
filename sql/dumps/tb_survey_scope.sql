create table tb_survey_scope
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
    on tb_survey_scope (survey_id);

