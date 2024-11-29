
-- 创建库
create database if not exists hot_news;

-- 切换库
use hot_news;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '用户' collate = utf8mb4_unicode_ci;

-- 热点信息接口地址
create table if not exists hot_api(
  id           bigint auto_increment comment 'id' primary key,
  apiName   varchar(128) not null comment '接口名称',
  apiURL varchar(512) not null comment '接口地址',
  apiDescribe varchar(256) null comment '接口描述',
  createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
  updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
  isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '热点信息接口地址' collate = utf8mb4_unicode_ci;

-- 任务中心
create table if not exists task
(
    id              bigint auto_increment comment 'id' primary key,
    taskName        varchar(256)                       not null comment '任务名称',
    platFormAccount varchar(256)                       not null comment '第三方账号',
    userId          bigint                             not null comment '用户id',
    hotNewTitle     varchar(512)                       not null comment '热点标题',
    hotPlatForm     varchar(128)                       not null comment '热点平台',
    taskStatus      tinyint  default 0                 not null comment '任务状态',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除'
) comment '任务中心表' collate = utf8mb4_unicode_ci;


-- 任务中心
create table if not exists task
(
    id              bigint auto_increment comment 'id' primary key,
    taskName        varchar(256)                       not null comment '任务名称',
    platFormAccount varchar(256)                       not null comment '第三方账号',
    userId          bigint                             not null comment '用户id',
    hotNewTitle     varchar(512)                       not null comment '热点标题',
    hotPlatForm     varchar(128)                       not null comment '热点平台',
    taskStatus      tinyint  default 0                 not null comment '任务状态',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除'
) comment '任务中心表' collate = utf8mb4_unicode_ci;
