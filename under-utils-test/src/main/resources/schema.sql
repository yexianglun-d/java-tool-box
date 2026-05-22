DROP TABLE IF EXISTS t_user;

CREATE TABLE t_user (
    id BIGINT NOT NULL COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) COMMENT '邮箱',
    age INT COMMENT '年龄',
    status INT DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    create_time DATETIME(6) COMMENT '创建时间',
    update_time DATETIME(6) COMMENT '修改时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '修改人ID',
    deleted INT DEFAULT 0 COMMENT '逻辑删除(0:未删除,1:已删除)',
    PRIMARY KEY (id)
);
