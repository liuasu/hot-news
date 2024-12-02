/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.1.50_3306
 Source Server Type    : MySQL
 Source Server Version : 80040
 Source Host           : 192.168.1.50:3306
 Source Schema         : hot_news

 Target Server Type    : MySQL
 Target Server Version : 80040
 File Encoding         : 65001

 Date: 30/11/2024 00:14:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hot_api
-- ----------------------------
DROP TABLE IF EXISTS `hot_api`;
CREATE TABLE `hot_api`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `platform` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台',
  `apiName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接口名称',
  `apiURL` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接口地址',
  `apiDescribe` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '接口描述',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '热点信息接口地址' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hot_api
-- ----------------------------
INSERT INTO `hot_api` VALUES (1, 'jinrirebang', '今日热榜', 'https://api.pearktrue.cn/api/dailyhot/?title=', '查看各大榜单的今日热榜排行（哔哩哔哩，百度，知乎，百度贴吧，少数派，IT之家，澎湃新闻，今日头条，微博热搜，36氪，稀土掘金，腾讯新闻）', '2024-11-18 21:41:00', '2024-11-19 20:48:09', 0);
INSERT INTO `hot_api` VALUES (2, 'douyin', '抖音当日热点', 'https://www.douyin.com/aweme/v1/web/hot/search/list/?device_platform=webapp&aid=6383&channel=channel_pc_web&detail_list=1', 'detail_list参数类似第几页', '2024-11-18 22:01:53', '2024-11-19 20:48:09', 0);
INSERT INTO `hot_api` VALUES (3, 'bilibili', '哔哩哔哩当日热点', 'https://api.bilibili.com/x/web-interface/ranking/v2?tid=0&type=all&%s', '%s后面接获取到的wbi才能获取热点', '2024-11-18 22:04:09', '2024-11-20 12:08:58', 0);
INSERT INTO `hot_api` VALUES (4, '51cto', '51CTO推荐榜', 'https://api-media.51cto.com/index/index/recommend', '51CTO推荐榜', '2024-11-18 23:11:41', '2024-11-19 20:48:09', 0);
INSERT INTO `hot_api` VALUES (5, 'toutiao', '今日头条热榜', 'https://www.toutiao.com/hot-event/hot-board/?origin=toutiao_pc', '今日头条热榜', '2024-11-18 23:11:41', '2024-11-19 20:48:09', 0);
INSERT INTO `hot_api` VALUES (6, 'toutiao_getUserInfo', '获取登录用户', 'https://mp.toutiao.com/mp/agw/creator_center/user_info?app_id=1231', '今日头条获取登录的用户信息', '2024-11-29 22:04:11', '2024-11-29 22:04:11', 0);
INSERT INTO `hot_api` VALUES (7, 'toutiao_user_login_status', '用户登录状态', 'https://mp.toutiao.com/mp/agw/media/user_login_status_api?is_new_register=1\r\n', '今日头条用户登录状态', '2024-11-29 22:44:48', '2024-11-29 22:44:48', 0);

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `taskName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `platFormAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第三方账号',
  `userId` bigint NOT NULL COMMENT '用户id',
  `hotNewTitle` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '热点标题',
  `hotUrl` varchar(1028) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'url',
  `hotPlatForm` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '热点平台',
  `taskStatus` tinyint NOT NULL DEFAULT 0 COMMENT '任务状态',
  `taskType` tinyint NOT NULL COMMENT '任务类型',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1862320888018223106 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '任务中心表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task
-- ----------------------------
INSERT INTO `task` VALUES (1860295996099825665, '林诗栋不敌张本智和无缘决赛', '1815611467541508', 1858497270280724482, '林诗栋不敌张本智和无缘决赛', NULL, 'toutiao', 0, 0, '2024-11-23 20:15:03', '2024-11-28 10:34:02', 1);
INSERT INTO `task` VALUES (1861326950151053313, '美巡逻机过航台海 解放军跟监警戒', '1815611467541508', 1858497270280724482, '美巡逻机过航台海 解放军跟监警戒', NULL, 'toutiao', 0, 0, '2024-11-26 16:31:41', '2024-11-28 10:34:01', 1);
INSERT INTO `task` VALUES (1861696683654627330, '副部朱芝松被查 身边数人同时“失联”', '1815611467541508', 1858497270280724482, '副部朱芝松被查 身边数人同时“失联”', 'https://www.toutiao.com/trending/7441843934780722727/', 'toutiao', 0, 0, '2024-11-27 17:00:53', '2024-11-28 10:33:54', 1);
INSERT INTO `task` VALUES (1861955990432026626, '张子宇：17岁2米24的内线统治力', NULL, 1858497270280724482, '张子宇：17岁2米24的内线统治力', 'https://www.toutiao.com/trending/7441948937189294106/', 'toutiao', 0, 0, '2024-11-28 10:11:16', '2024-11-28 10:31:26', 1);
INSERT INTO `task` VALUES (1861961766143852546, '中国力量推动全球产业链更加高效', '1815611467541599', 1858497270280724482, '中国力量推动全球产业链更加高效', 'https://www.toutiao.com/article/7441847471476621843', 'toutiao', 0, 0, '2024-11-28 10:34:13', '2024-11-28 15:00:18', 1);
INSERT INTO `task` VALUES (1861963056047521793, '第十一批在韩志愿军烈士遗骸回国', '1815611467541599', 1858497270280724482, '第十一批在韩志愿军烈士遗骸回国', 'https://www.toutiao.com/trending/7441402083946971173/', 'toutiao', 0, 0, '2024-11-28 10:39:21', '2024-11-28 15:00:19', 1);
INSERT INTO `task` VALUES (1861974389543469058, '野猪将村里新坟拱开 逝者衣服遍地', '1815611467541501', 1858497270280724482, '野猪将村里新坟拱开 逝者衣服遍地', 'https://www.toutiao.com/trending/7441129873944985651/', 'toutiao', 0, 0, '2024-11-28 11:24:23', '2024-11-28 15:00:20', 1);
INSERT INTO `task` VALUES (1862042110201200642, '女子取2.5万遭银行挨个电话核实', NULL, 1858497270280724482, '女子取2.5万遭银行挨个电话核实', 'https://www.toutiao.com/trending/7442198270492970505/', 'toutiao', 0, 0, '2024-11-28 15:53:29', '2024-11-28 15:53:29', 0);
INSERT INTO `task` VALUES (1862042120833761282, '华龙一号核电基地首台机组并网发电', NULL, 1858497270280724482, '华龙一号核电基地首台机组并网发电', 'https://www.toutiao.com/article/7442129600068600370', 'toutiao', 0, 0, '2024-11-28 15:53:31', '2024-11-28 15:53:31', 0);
INSERT INTO `task` VALUES (1862318636742328322, '胖东来养活了超300个代购账号', NULL, 1858497270280724482, '胖东来养活了超300个代购账号', 'https://www.toutiao.com/trending/7441612038515982373/', 'toutiao', 0, 0, '2024-11-29 10:12:18', '2024-11-29 10:12:17', 0);
INSERT INTO `task` VALUES (1862320888018223105, '读研女生被餐馆阿姨介绍自己儿子', NULL, 1858497270280724482, '读研女生被餐馆阿姨介绍自己儿子', 'https://www.toutiao.com/trending/7442205718608265266/', 'toutiao', 0, 0, '2024-11-29 10:21:15', '2024-11-29 10:21:14', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `userName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `userAvatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户头像',
  `userRole` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1860246571201683459 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1858497270280724482, 'liaosisi', 'b0dd3697a192885d7c055db46155b26a', 'liaosisi', 'https://www.code-nav.cn/logo.png', 'admin', '2024-11-18 21:07:33', '2024-11-21 17:12:55', 0);
INSERT INTO `user` VALUES (1860236757050396674, 'zhangwanting', 'b0dd3697a192885d7c055db46155b26a', NULL, 'https://www.code-nav.cn/logo.png', 'user', '2024-11-23 16:19:39', '2024-11-23 16:23:32', 0);
INSERT INTO `user` VALUES (1860246571201683458, 'xiaoheizi', 'b0dd3697a192885d7c055db46155b26a', NULL, 'https://www.code-nav.cn/logo.png', 'user', '2024-11-23 16:58:39', '2024-11-23 16:58:39', 0);

SET FOREIGN_KEY_CHECKS = 1;
