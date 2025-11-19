create table sys_user_post
(
    user_id bigint not null comment '用户ID',
    post_id bigint not null comment '岗位ID',
    primary key (user_id, post_id)
)
    comment '用户与岗位关联表' collate = utf8mb4_general_ci
                               row_format = DYNAMIC;

INSERT INTO ruoyi.sys_user_post (user_id, post_id) VALUES (1, 1);
