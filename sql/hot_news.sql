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

 Date: 10/12/2024 21:15:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_config
-- ----------------------------
DROP TABLE IF EXISTS `ai_config`;
CREATE TABLE `ai_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户id',
  `appId` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'appid',
  `apiKey` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'apiKey',
  `apiSecret` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'apiSecret',
  `aiPlatForm` tinyint NOT NULL COMMENT 'ai平台',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1864578713930989570 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'ai 秘钥配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_config
-- ----------------------------
INSERT INTO `ai_config` VALUES (1864568292754915330, 1858497270280724482, '3ce98aad', '999036d18c01d38fa64a2a7d9cd3ca7f', 'YzhlYzhhMzE1MDNkMzlmMjBkYTBmMzE4', 1, '2024-12-05 15:11:38', '2024-12-05 15:18:51', 0);
INSERT INTO `ai_config` VALUES (1864578510876344321, 1858497270280724482, '', '4839ee1b15fbcb5e436413041b51684f.rdaciz4ItpXo3b5I', '', 2, '2024-12-05 15:52:14', '2024-12-05 15:52:13', 0);
INSERT INTO `ai_config` VALUES (1864578713930989570, 1858497270280724482, '', '1iGs2zzIuuxXusq7PfauhBOtuGVo3aCvc4zBgUZ8I9JpgWVFJ0csTVhrHcknT0qGN', '', 3, '2024-12-05 15:53:02', '2024-12-05 15:53:01', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '热点信息接口地址' ROW_FORMAT = Dynamic;

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
INSERT INTO `hot_api` VALUES (8, 'toutiao_login', '登录', 'https://mp.toutiao.com/auth/page/login', '今日头条登录', '2024-12-01 21:01:56', '2024-12-01 21:01:56', 0);
INSERT INTO `hot_api` VALUES (9, 'toutiao_page_index', '首页', 'https://mp.toutiao.com/profile_v4/index', '今日头条首页', '2024-12-01 21:03:20', '2024-12-01 21:03:20', 0);
INSERT INTO `hot_api` VALUES (10, 'thepaper', '澎湃新闻热点', 'https://cache.thepaper.cn/contentapi/wwwIndex/rightSidebar', '澎湃新闻热点', '2024-12-02 21:49:36', '2024-12-02 21:49:36', 0);
INSERT INTO `hot_api` VALUES (11, 'thirtysix', '36氪热点', 'https://gateway.36kr.com/api/mis/nav/home/nav/rank/hot', '36氪热点', '2024-12-03 10:51:31', '2024-12-03 10:51:31', 0);
INSERT INTO `hot_api` VALUES (12, 'zhipu_ai', '智普ai模型调用地址', 'https://open.bigmodel.cn/api/paas/v4/chat/completions', '智普ai模型调用地址', '2024-12-05 15:54:32', '2024-12-05 15:54:32', 0);
INSERT INTO `hot_api` VALUES (13, 'jieyue_xingchen_ai', '阶跃星辰ai模型调用地址', 'https://api.stepfun.com/v1/chat/completions', '阶跃星辰ai模型调用地址', '2024-12-05 15:55:26', '2024-12-05 15:55:28', 0);
INSERT INTO `hot_api` VALUES (14, 'toutiao_article_publish', '今日头条发文地址', 'https://mp.toutiao.com/profile_v4/graphic/publish', '今日头条发文地址', '2024-12-05 21:41:03', '2024-12-05 21:41:03', 0);
INSERT INTO `hot_api` VALUES (15, 'qq_news', '腾讯新闻热点', 'https://r.inews.qq.com/gw/event/hot_ranking_list?page_size=20', '腾讯新闻热点', '2024-12-08 22:12:55', '2024-12-08 22:12:55', 0);
INSERT INTO `hot_api` VALUES (16, 'wangyi', '网易新闻热点', 'https://m.163.com/fe/api/hot/news/flow', '网易新闻热点', '2024-12-08 22:16:12', '2024-12-08 22:16:15', 0);

-- ----------------------------
-- Table structure for prompt
-- ----------------------------
DROP TABLE IF EXISTS `prompt`;
CREATE TABLE `prompt`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `promptName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `promptTemplate` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提示词模板',
  `userId` bigint NOT NULL COMMENT '用户id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1864650343881408514 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'ai提示词表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of prompt
-- ----------------------------
INSERT INTO `prompt` VALUES (1864650154688937986, 'default', '我在运营一个今日头条号,受众都是娱乐粉丝人群。\\n你是一个资深的网络文章作家,也有多年的互联网从业经验,擅长写用户爱看、吸引人的爆款网络文章。\\n\\n===严格的写作规范===\\n1. 绝对禁止使用以下过渡词和连接词(这是硬性要求,任何情况下都不能出现):\\n[禁用词列表]\\n⚠️ 以下词汇和表达方式被严格禁止使用，发现立即重写：\\n- 过渡词：首先、其次、然后、接着、最后\\n- 推测词：或许、又或者、可能、也许\\n- 补充词：另外、此外、况且、而且\\n- 总结词：总的来说、综上所述、总而言之\\n- 口语词：说到这里、说到这儿\\n- 废话词：众所周知、大家都知道\\n- 转折词：事实上、实际上、其实、然而、但是、不过、当然\\n\\n\\n2. 替代表达方式:\\n- 使用破折号(—)直接引出新观点\\n- 使用冒号(:)开启新话题\\n- 运用数字(1/2/3)标记重点\\n- 以问句引导新段落\\n- 通过对比展开论述\\n- 用具体数据切入主题\\n\\n===创作格式===\\n创作主题:\\n{需要创作的文章主题或方向}\\n\\n相关素材:\\n{相关的新闻、数据或背景信息}\\n\\n输出格式(严格遵守):\\n\\\'【【【【【\\\'\\n{爆款标题,18字以内,富有争议性或悬疑性}\\n\\\'【【【【【\\\'\\n{正文内容,要求:\\n1. 字数必须大于1300字\\n2. 原创度>85%\\n3. 观点独特有深度\\n4. 结构完整,层次分明\\n5. 语言生动,有感染力\\n6. 设置2-3个互动点\\n7. 结尾留有悬念\\n8. 严格遵守禁用词规范}\\n\\n===段落过渡示例===\\n❌错误示范:\\n\\\"首先来看华为的技术...\\\"\\n\\\"或许是因为市场需求...\\\"\\n\\\"另外在性能方面...\\\"\\n\\\"总的来说这款手机...\\\"\\n\\n✅正确示范:\\n\\\"华为的技术实力—\\\"\\n\\\"市场数据显示:\\\"\\n\\\"性能测试结果:\\\"\\n\\\"这款手机的价值体现在:\\\"\\n\\n===重要提醒===\\n- 本提示词对所有AI模型都适用\\n- 以上规范必须严格执行\\n- 违反规范的内容将被拒绝\\n- 需要反复检查确保无禁用词\\n\\n示例文章:\\n\\\'【【【【【\\\'\\n华为Mate70爆卖背后:谁在慌?谁在抢?\\n\\\'【【【【【\\\'\\n{文章正文...}\\n\r\n', 1858497270280724482, '2024-12-05 20:36:55', '2024-12-07 14:16:33', 0);
INSERT INTO `prompt` VALUES (1864650343881408514, 'default2', '我在运营一个今日头条号,受众都是娱乐粉丝人群。\\n你是一个资深的网络文章作家,也有多年的互联网从业经验,擅长写用户爱看、吸引人的爆款网络文章。\\n\\n[写作任务]\\n1. 生成一个爆款主标题(18字内)\\n2. 创作正文内容(必须在1300字以上)\\n3. 最后必须生成3个备选标题\\n\\n===严格的写作规范===\\n1. 绝对禁止使用以下过渡词和连接词(这是硬性要求,任何情况下都不能出现):\\n[禁用词列表]\\n⚠️ 以下词汇和表达方式被严格禁止使用，发现立即重写：\\n- 过渡词：首先、其次、然后、接着、最后\\n- 推测词：或许、又或者、可能、也许\\n- 补充词：另外、此外、况且、而且\\n- 总结词：总的来说、综上所述、总而言之\\n- 口语词：说到这里、说到这儿\\n- 废话词：众所周知、大家都知道\\n- 转折词：事实上、实际上、其实、然而、但是、不过、当然\\n\\n2. 替代表达方式:\\n- 使用破折号(—)直接引出新观点\\n- 使用冒号(:)开启新话题\\n- 运用数字(1/2/3)标记重点\\n- 以问句引导新段落\\n- 通过对比展开论述\\n- 用具体数据切入主题\\n\\n[输出格式要求]\\n\\\'【【【【【\\\'\\n{爆款标题,18字以内,富有争议性或悬疑性}\\n\\n\\\'【【【【【\\\'\\n{正文内容,要求:\\n1. 字数必须大于1300字\\n2. 原创度>85%\\n3. 观点独特有深度\\n4. 结构完整,层次分明\\n5. 语言生动,有感染力\\n6. 设置2-3个互动点\\n7. 结尾留有悬念\\n8. 严格遵守禁用词规范}\\n\\n===段落过渡示例===\\n❌错误示范:\\n\\\"首先来看华为的技术...\\\"\\n\\\"或许是因为市场需求...\\\"\\n\\\"另外在性能方面...\\\"\\n\\\"总的来说这款手机...\\\"\\n\\n✅正确示范:\\n\\\"华为的技术实力—\\\"\\n\\\"市场数据显示:\\\"\\n\\\"性能测试结果:\\\"\\n\\\"这款手机的价值体现在:\\\"\\n\\n===重要提醒===\\n- 本提示词对所有AI模型都适用\\n- 以上规范必须严格执行\\n- 违反规范的内容将被拒绝\\n- 需要反复检查确保无禁用词\\n- ⚠️必须按照三个部分顺序输出完整内容\\n\\n[输出示例]\\n\\\'【【【【【\\\'\\n华为Mate70爆卖背后:谁在慌?谁在抢?\\n\\\'【【【【【\\\'\\n{文章正文...}\\n\r\n', 1858497270280724482, '2024-12-05 20:37:40', '2024-12-07 14:16:17', 0);

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `taskName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `platFormAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第三方账号',
  `platForm` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第三方账号平台',
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
) ENGINE = InnoDB AUTO_INCREMENT = 1866357808083841026 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '任务中心表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task
-- ----------------------------
INSERT INTO `task` VALUES (1860295996099825665, '林诗栋不敌张本智和无缘决赛', '1815611467541508', '', 1858497270280724482, '林诗栋不敌张本智和无缘决赛', NULL, 'toutiao', 0, 0, '2024-11-23 20:15:03', '2024-11-28 10:34:02', 1);
INSERT INTO `task` VALUES (1861326950151053313, '美巡逻机过航台海 解放军跟监警戒', '1815611467541508', '', 1858497270280724482, '美巡逻机过航台海 解放军跟监警戒', NULL, 'toutiao', 0, 0, '2024-11-26 16:31:41', '2024-11-28 10:34:01', 1);
INSERT INTO `task` VALUES (1861696683654627330, '副部朱芝松被查 身边数人同时“失联”', '1815611467541508', '', 1858497270280724482, '副部朱芝松被查 身边数人同时“失联”', 'https://www.toutiao.com/trending/7441843934780722727/', 'toutiao', 0, 0, '2024-11-27 17:00:53', '2024-11-28 10:33:54', 1);
INSERT INTO `task` VALUES (1861955990432026626, '张子宇：17岁2米24的内线统治力', NULL, '', 1858497270280724482, '张子宇：17岁2米24的内线统治力', 'https://www.toutiao.com/trending/7441948937189294106/', 'toutiao', 0, 0, '2024-11-28 10:11:16', '2024-11-28 10:31:26', 1);
INSERT INTO `task` VALUES (1861961766143852546, '中国力量推动全球产业链更加高效', '1815611467541599', '', 1858497270280724482, '中国力量推动全球产业链更加高效', 'https://www.toutiao.com/article/7441847471476621843', 'toutiao', 0, 0, '2024-11-28 10:34:13', '2024-11-28 15:00:18', 1);
INSERT INTO `task` VALUES (1861963056047521793, '第十一批在韩志愿军烈士遗骸回国', '1815611467541599', '', 1858497270280724482, '第十一批在韩志愿军烈士遗骸回国', 'https://www.toutiao.com/trending/7441402083946971173/', 'toutiao', 0, 0, '2024-11-28 10:39:21', '2024-11-28 15:00:19', 1);
INSERT INTO `task` VALUES (1861974389543469058, '野猪将村里新坟拱开 逝者衣服遍地', '1815611467541501', '', 1858497270280724482, '野猪将村里新坟拱开 逝者衣服遍地', 'https://www.toutiao.com/trending/7441129873944985651/', 'toutiao', 0, 0, '2024-11-28 11:24:23', '2024-11-28 15:00:20', 1);
INSERT INTO `task` VALUES (1862042110201200642, '女子取2.5万遭银行挨个电话核实', NULL, '', 1858497270280724482, '女子取2.5万遭银行挨个电话核实', 'https://www.toutiao.com/trending/7442198270492970505/', 'toutiao', 0, 0, '2024-11-28 15:53:29', '2024-12-02 15:41:32', 1);
INSERT INTO `task` VALUES (1862042120833761282, '华龙一号核电基地首台机组并网发电', NULL, '', 1858497270280724482, '华龙一号核电基地首台机组并网发电', 'https://www.toutiao.com/article/7442129600068600370', 'toutiao', 0, 0, '2024-11-28 15:53:31', '2024-12-02 15:41:31', 1);
INSERT INTO `task` VALUES (1862318636742328322, '胖东来养活了超300个代购账号', NULL, '', 1858497270280724482, '胖东来养活了超300个代购账号', 'https://www.toutiao.com/trending/7441612038515982373/', 'toutiao', 0, 0, '2024-11-29 10:12:18', '2024-12-02 15:41:30', 1);
INSERT INTO `task` VALUES (1862320888018223105, '读研女生被餐馆阿姨介绍自己儿子', NULL, '', 1858497270280724482, '读研女生被餐馆阿姨介绍自己儿子', 'https://www.toutiao.com/trending/7442205718608265266/', 'toutiao', 0, 0, '2024-11-29 10:21:15', '2024-12-02 15:41:30', 1);
INSERT INTO `task` VALUES (1863426979359711234, '400斤12岁女孩准备手术保命', NULL, '', 1858497270280724482, '400斤12岁女孩准备手术保命', 'https://www.toutiao.com/trending/7443635182252625458/', 'toutiao', 0, 0, '2024-12-02 11:36:27', '2024-12-02 15:41:29', 1);
INSERT INTO `task` VALUES (1863427056081920001, '老人后仰摔倒男子飞速伸脚垫头', NULL, '', 1858497270280724482, '老人后仰摔倒男子飞速伸脚垫头', 'https://www.toutiao.com/trending/7443118980719575076/', 'toutiao', 0, 0, '2024-12-02 11:36:46', '2024-12-02 15:41:28', 1);
INSERT INTO `task` VALUES (1863427232016195585, '蹚出独具特色的卫星导航探索之路', NULL, '', 1858497270280724482, '蹚出独具特色的卫星导航探索之路', 'https://www.toutiao.com/article/7443609472766968347', 'toutiao', 0, 0, '2024-12-02 11:37:28', '2024-12-02 15:41:27', 1);
INSERT INTO `task` VALUES (1863484162298429442, '网红彩虹夫妇女儿确诊ABO溶血', NULL, '', 1858497270280724482, '网红彩虹夫妇女儿确诊ABO溶血', 'https://www.toutiao.com/trending/7443085343240814618/', 'toutiao', 0, 0, '2024-12-02 15:23:41', '2024-12-02 15:41:26', 1);
INSERT INTO `task` VALUES (1865328666760482818, '韩民主党一一喊执政党议员返回投票', NULL, '', 1858497270280724482, '韩民主党一一喊执政党议员返回投票', 'https://www.toutiao.com/trending/7445593708956782117/', 'toutiao', 0, 0, '2024-12-07 17:33:05', '2024-12-10 11:50:15', 1);
INSERT INTO `task` VALUES (1865335214798868482, '埃尔多安发声支持叙反政府武装', '2896548336522260', '', 1858497270280724482, '埃尔多安发声支持叙反政府武装', 'https://www.toutiao.com/trending/7444377174184296485/', 'toutiao', 1, 0, '2024-12-07 17:59:06', '2024-12-10 11:50:15', 1);
INSERT INTO `task` VALUES (1865350461517508609, '盘点2024闪耀中外的国风顶流', '2896548336522260', '', 1858497270280724482, '盘点2024闪耀中外的国风顶流', 'https://www.toutiao.com/article/7445483548175352355', 'toutiao', 1, 0, '2024-12-07 18:59:41', '2024-12-10 11:50:14', 1);
INSERT INTO `task` VALUES (1866112738285891585, '85后海归女硕士，做中国最冷门的职业', NULL, '', 1858497270280724482, '85后海归女硕士，做中国最冷门的职业', 'https://img.36krcdn.com/hsossms/20241209/v2_aab3672d34e145098cf9faa2a8d4afa4@5888275_oswg918390oswg1053oswg495_img_png?x-oss-process=image/resize,m_mfit,w_600,h_400,limit_0/crop,w_600,h_400,g_center', '', 0, 0, '2024-12-09 21:28:42', '2024-12-10 11:50:15', 1);
INSERT INTO `task` VALUES (1866327305872048129, '英伟达涉嫌违反反垄断法，市场监管总局决定立案调查：涉5年前收购迈络思', NULL, '', 1858497270280724482, '英伟达涉嫌违反反垄断法，市场监管总局决定立案调查：涉5年前收购迈络思', 'https://www.thepaper.cn/newsDetail_forward_29594984', '', 0, 0, '2024-12-10 11:41:19', '2024-12-10 11:50:14', 1);
INSERT INTO `task` VALUES (1866329677197246465, '英伟达涉嫌违反反垄断法，市场监管总局决定立案调查：涉5年前收购迈络思', NULL, '', 1858497270280724482, '英伟达涉嫌违反反垄断法，市场监管总局决定立案调查：涉5年前收购迈络思', 'https://www.thepaper.cn/newsDetail_forward_29594984', 'thepaper', 0, 0, '2024-12-10 11:50:44', '2024-12-10 11:56:25', 1);
INSERT INTO `task` VALUES (1866330159357657090, '英伟达涉嫌违反反垄断法，市场监管总局决定立案调查：涉5年前收购迈络思', '2896548336522260', '', 1858497270280724482, '英伟达涉嫌违反反垄断法，市场监管总局决定立案调查：涉5年前收购迈络思', 'https://www.thepaper.cn/newsDetail_forward_29594984', 'thepaper', 0, 0, '2024-12-10 11:52:39', '2024-12-10 14:57:18', 1);
INSERT INTO `task` VALUES (1866357808083841025, '13年里949万元！小学校长为他人在学生入学、转学、分班等受贿', '2896548336522260', '', 1858497270280724482, '13年里949万元！小学校长为他人在学生入学、转学、分班等受贿', 'https://www.thepaper.cn/newsDetail_forward_29592800', 'thepaper', 0, 0, '2024-12-10 13:42:31', '2024-12-10 14:57:17', 1);
INSERT INTO `task` VALUES (1866375456230252546, '马云回到舞台中央', '2896548336522260', 'toutiao', 1858497270280724482, '马云回到舞台中央', 'https://img.36krcdn.com/hsossms/20241210/v2_a92bbdb985dc47489a72d0d420226d24@5888275_oswg791452oswg1053oswg495_img_png?x-oss-process=image/resize,m_mfit,w_600,h_400,limit_0/crop,w_600,h_400,g_center', 'thirtysix', 0, 0, '2024-12-10 14:52:39', '2024-12-10 14:57:16', 1);
INSERT INTO `task` VALUES (1866376910789730306, '马云回到舞台中央', NULL, NULL, 1858497270280724482, '马云回到舞台中央', 'https://www.36kr.com/p/3071816389587847', 'thirtysix', 0, 0, '2024-12-10 14:58:26', '2024-12-10 14:58:25', 0);
INSERT INTO `task` VALUES (1866381743349325825, '马云回到舞台中央', NULL, NULL, 1860246571201683458, '马云回到舞台中央', 'https://www.36kr.com/p/3071816389587847', 'thirtysix', 0, 0, '2024-12-10 15:17:38', '2024-12-10 15:27:09', 1);
INSERT INTO `task` VALUES (1866384265711476737, '马云回到舞台中央', NULL, NULL, 1860246571201683458, '马云回到舞台中央', 'https://www.36kr.com/p/3071816389587847', 'thirtysix', 0, 0, '2024-12-10 15:27:39', '2024-12-10 15:27:39', 0);

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
