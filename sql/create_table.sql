create table ai_article_creation_log
(
    id                bigint auto_increment comment 'id'
        primary key,
    aiPlatForm        varchar(64)   default ''                null comment 'ai平台',
    hotTitle          varchar(512)  default ''                null comment '热点标题',
    hotUrl            varchar(1024) default ''                null comment '热点链接',
    aiCreationTitle   varchar(256)  default ''                null comment 'ai生成标题',
    aiCreationContext text                                    null comment 'ai生成内容',
    userId            varchar(128)  default ''                null comment 'userId',
    createTime        datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime        datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment 'ai文章生成记录' collate = utf8mb4_unicode_ci;

create table ai_config
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             not null comment '用户id',
    appId      varchar(256)                       null comment 'appid',
    apiKey     varchar(256)                       not null comment 'apiKey',
    apiSecret  varchar(256)                       null comment 'apiSecret',
    aiPlatForm tinyint                            not null comment 'ai平台',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment 'ai 秘钥配置' collate = utf8mb4_unicode_ci;

create table dict
(
    id              bigint auto_increment comment 'id'
        primary key,
    dictName        varchar(100)                       null comment '字典名称',
    dictLabelKey    varchar(100)                       null comment '字典标签(key)',
    dictLabelValues varchar(100)                       null comment '字典标签(values)',
    dictChildId     bigint   default 0                 not null comment '字典类型',
    status          tinyint  default 0                 not null comment '状态（0正常 1停用）',
    userId          bigint                             not null comment '用户id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除'
)
    comment '字典表' collate = utf8mb4_unicode_ci;

create table hot_api
(
    id          bigint auto_increment comment 'id'
        primary key,
    platform    varchar(64)                        not null comment '平台',
    apiName     varchar(128)                       not null comment '接口名称',
    apiURL      varchar(512)                       not null comment '接口地址',
    apiDescribe varchar(256)                       null comment '接口描述',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '热点信息接口地址' collate = utf8mb4_unicode_ci;

create table oper_log
(
    id            bigint auto_increment comment 'id'
        primary key,
    className     varchar(200)  default '0' null comment '类名',
    method        varchar(200)  default ''  null comment '方法名称',
    requestMethod varchar(10)   default ''  null comment '请求方式',
    operUser      varchar(50)   default ''  null comment '操作人员',
    operUrl       varchar(255)  default ''  null comment '请求URL',
    operIp        varchar(128)  default ''  null comment '主机地址',
    operParam     varchar(2000) default ''  null comment '请求参数',
    jsonResult    varchar(2000) default ''  null comment '返回参数',
    status        bigint        default 0   null comment '操作状态（0正常 1异常）',
    errorMsg      varchar(2000) default ''  null comment '错误消息',
    operTime      datetime                  null comment '操作时间',
    costTime      bigint        default 0   null comment '消耗时间'
)
    comment '操作日志记录' collate = utf8mb4_unicode_ci;

create table prompt
(
    id             bigint auto_increment comment 'id'
        primary key,
    promptName     varchar(128)                       not null comment '模板名称',
    promptTemplate text                               not null comment '提示词模板',
    userId         bigint                             not null comment '用户id',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除'
)
    comment 'ai提示词表' collate = utf8mb4_unicode_ci;

create table task
(
    id              bigint auto_increment comment 'id'
        primary key,
    taskName        varchar(256)                       not null comment '任务名称',
    platFormAccount varchar(256)                       null comment '第三方账号',
    platForm        varchar(64)                        null comment '第三方账号平台',
    userId          bigint                             not null comment '用户id',
    hotNewTitle     varchar(512)                       not null comment '热点标题',
    hotUrl          varchar(1028)                      null comment 'url',
    hotPlatForm     varchar(128)                       not null comment '热点平台',
    taskStatus      tinyint  default 0                 not null comment '任务状态',
    taskType        tinyint                            not null comment '任务类型',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除'
)
    comment '任务中心表' collate = utf8mb4_unicode_ci;

create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
)
    comment '用户' collate = utf8mb4_unicode_ci;

