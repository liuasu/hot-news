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

 Date: 20/11/2024 12:09:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hot_api
-- ----------------------------
-- 创建库
create database if not exists hot_news;

-- 切换库
use hot_news;

-- 热点信息接口地址
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

-- ----------------------------
-- Table structure for user
-- ----------------------------

-- 用户表
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
) ENGINE = InnoDB AUTO_INCREMENT = 1858497270280724483 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1858497270280724482, 'liaosisi', 'b0dd3697a192885d7c055db46155b26a', 'liaosisi', NULL, 'admin', '2024-11-18 21:07:33', '2024-11-18 21:32:47', 0);

SET FOREIGN_KEY_CHECKS = 1;
