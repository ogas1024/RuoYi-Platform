create table tb_category
(
    id            int auto_increment comment '类别ID'
        primary key,
    category_name varchar(100) null comment '类别名称'
)
    comment '图书类别表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

INSERT INTO ruoyi.tb_category (id, category_name) VALUES (1, '计算机科学');
INSERT INTO ruoyi.tb_category (id, category_name) VALUES (2, '文学小说');
INSERT INTO ruoyi.tb_category (id, category_name) VALUES (3, '历史传记');
INSERT INTO ruoyi.tb_category (id, category_name) VALUES (4, '科普读物');
INSERT INTO ruoyi.tb_category (id, category_name) VALUES (5, '商业管理');
INSERT INTO ruoyi.tb_category (id, category_name) VALUES (6, '幼儿教育');
