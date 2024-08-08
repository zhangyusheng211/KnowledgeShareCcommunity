/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 8.0.12 : Database - wx
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`mogu_wechat` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `mogu_wechat`;


DROP TABLE IF EXISTS `sys_captcha`;

CREATE TABLE `sys_captcha` (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'uuid',
  `code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证码',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='系统验证码';

/*Data for the table `sys_captcha` */

/*Table structure for table `sys_config` */

DROP TABLE IF EXISTS `sys_config`;

CREATE TABLE `sys_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `param_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'key',
  `param_value` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'value',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态   0：隐藏   1：显示',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `param_key` (`param_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='系统配置信息表';

/*Data for the table `sys_config` */

insert  into `sys_config`(`id`,`param_key`,`param_value`,`status`,`remark`) values (1,'CLOUD_STORAGE_CONFIG_KEY','{\"type\":3,\"qiniuDomain\":\"\",\"qiniuPrefix\":\"\",\"qiniuAccessKey\":\"\",\"qiniuSecretKey\":\"\",\"qiniuBucketName\":\"\",\"aliyunDomain\":\"\",\"aliyunPrefix\":\"\",\"aliyunEndPoint\":\"\",\"aliyunAccessKeyId\":\"\",\"aliyunAccessKeySecret\":\"\",\"aliyunBucketName\":\"\",\"qcloudDomain\":\"\",\"qcloudPrefix\":\"\",\"qcloudAppId\":\"\",\"qcloudSecretId\":\"\",\"qcloudSecretKey\":\"\",\"qcloudBucketName\":\"\",\"qcloudRegion\":\"ap-guangzhou\"}',0,'云存储配置信息');

/*Table structure for table `sys_log` */

DROP TABLE IF EXISTS `sys_log`;

CREATE TABLE `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户操作',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '请求方法',
  `params` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '请求参数',
  `time` bigint(20) DEFAULT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'IP地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=324 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='系统日志';

/*Data for the table `sys_log` */

/*Table structure for table `sys_menu` */

DROP TABLE IF EXISTS `sys_menu`;

CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` int(11) DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '菜单图标',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=123 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='菜单管理';

/*Data for the table `sys_menu` */

insert  into `sys_menu`(`menu_id`,`parent_id`,`name`,`url`,`perms`,`type`,`icon`,`order_num`) values (1,0,'系统管理',NULL,NULL,0,'el-icon-s-tools',0),(2,1,'管理员列表','sys/user',NULL,1,'admin',1),(3,1,'角色管理','sys/role',NULL,1,'role',2),(4,1,'菜单管理','sys/menu',NULL,1,'menu',3),(6,0,'微信管理',NULL,NULL,0,'el-icon-s-promotion',1),(7,0,'内容管理','','',0,'el-icon-document-copy',2),(9,0,'日志报表','','',0,'el-icon-s-order',4),(15,2,'查看',NULL,'sys:user:list,sys:user:info',2,NULL,0),(16,2,'新增',NULL,'sys:user:save,sys:role:select',2,NULL,0),(17,2,'修改',NULL,'sys:user:update,sys:role:select',2,NULL,0),(18,2,'删除',NULL,'sys:user:delete',2,NULL,0),(19,3,'查看',NULL,'sys:role:list,sys:role:info',2,NULL,0),(20,3,'新增',NULL,'sys:role:save,sys:menu:list',2,NULL,0),(21,3,'修改',NULL,'sys:role:update,sys:menu:list',2,NULL,0),(22,3,'删除',NULL,'sys:role:delete',2,NULL,0),(23,4,'查看',NULL,'sys:menu:list,sys:menu:info',2,NULL,0),(24,4,'新增',NULL,'sys:menu:save,sys:menu:select',2,NULL,0),(25,4,'修改',NULL,'sys:menu:update,sys:menu:select',2,NULL,0),(26,4,'删除',NULL,'sys:menu:delete',2,NULL,0),(27,1,'参数管理','sys/config','sys:config:list,sys:config:info,sys:config:save,sys:config:update,sys:config:delete',1,'config',6),(29,9,'系统日志','sys/log','sys:log:list',1,'log',7),(30,1,'文件上传','oss/oss','sys:oss:all',1,'oss',6),(32,6,'公众号菜单','wx/wx-menu','',1,'log',0),(33,6,'素材管理','wx/wx-assets','',1,'',0),(41,7,'文章管理','wx/article',NULL,1,'config',6),(42,41,'查看',NULL,'wx:article:list,wx:article:info',2,NULL,6),(43,41,'新增',NULL,'wx:article:save',2,NULL,6),(44,41,'修改',NULL,'wx:article:update',2,NULL,6),(45,41,'删除',NULL,'wx:article:delete',2,NULL,6),(66,6,'自动回复规则','wx/msg-reply-rule',NULL,1,'config',6),(67,66,'查看',NULL,'wx:msgreplyrule:list,wx:msgreplyrule:info',2,NULL,6),(68,66,'新增',NULL,'wx:msgreplyrule:save',2,NULL,6),(69,66,'修改',NULL,'wx:msgreplyrule:update',2,NULL,6),(70,66,'删除',NULL,'wx:msgreplyrule:delete',2,NULL,6),(71,6,'模板消息','wx/msg-template',NULL,1,'config',6),(72,71,'查看',NULL,'wx:msgtemplate:list,wx:msgtemplate:info',2,NULL,6),(73,71,'新增',NULL,'wx:msgtemplate:save',2,NULL,6),(74,71,'修改',NULL,'wx:msgtemplate:update',2,NULL,6),(75,71,'删除',NULL,'wx:msgtemplate:delete',2,NULL,6),(81,9,'模版消息发送记录','wx/template-msg-log',NULL,1,'config',6),(84,81,'列表',NULL,'wx:templatemsglog:list',2,NULL,6),(85,81,'删除',NULL,'wx:templatemsglog:delete',2,NULL,6),(99,32,'更新公众号菜单','','wx:menu:save',2,'',0),(100,33,'查看','','wx:wxassets:list',2,'',0),(101,33,'新增修改','','wx:wxassets:save',2,'',0),(103,6,'带参二维码','wx/wx-qrcode',NULL,1,'config',6),(104,103,'查看',NULL,'wx:wxqrcode:list,wx:wxqrcode:info',2,NULL,6),(105,103,'新增',NULL,'wx:wxqrcode:save',2,NULL,6),(107,103,'删除',NULL,'wx:wxqrcode:delete',2,NULL,6),(108,6,'粉丝管理','wx/wx-user',NULL,1,'config',6),(109,108,'查看',NULL,'wx:wxuser:list,wx:wxuser:info',2,NULL,6),(110,108,'删除',NULL,'wx:wxuser:delete',2,NULL,6),(111,108,'同步','','wx:wxuser:save',2,'',6),(112,33,'删除','','wx:wxassets:delete',2,'',0),(113,6,'公众号消息','wx/wx-msg',NULL,1,'',6),(114,113,'查看',NULL,'wx:wxmsg:list,wx:wxmsg:info',2,NULL,6),(115,113,'新增',NULL,'wx:wxmsg:save',2,NULL,6),(117,113,'删除',NULL,'wx:wxmsg:delete',2,NULL,6),(118,6,'公众号账号','wx/wx-account',NULL,1,'config',6),(119,118,'查看',NULL,'wx:wxaccount:list,wx:wxaccount:info',2,NULL,6),(120,118,'新增',NULL,'wx:wxaccount:save',2,NULL,6),(121,118,'修改',NULL,'wx:wxaccount:update',2,NULL,6),(122,118,'删除',NULL,'wx:wxaccount:delete',2,NULL,6);

/*Table structure for table `sys_oss` */

DROP TABLE IF EXISTS `sys_oss`;

CREATE TABLE `sys_oss` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'URL地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='文件上传';

/*Data for the table `sys_oss` */

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='角色';

/*Data for the table `sys_role` */

/*Table structure for table `sys_role_menu` */

DROP TABLE IF EXISTS `sys_role_menu`;

CREATE TABLE `sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='角色与菜单对应关系';

/*Data for the table `sys_role_menu` */

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '密码',
  `salt` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '盐',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态  0：禁用   1：正常',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='系统用户';

/*Data for the table `sys_user` */

insert  into `sys_user`(`user_id`,`username`,`password`,`salt`,`email`,`mobile`,`status`,`create_user_id`,`create_time`) values (1,'admin','cdac762d0ba79875489f6a8b430fa8b5dfe0cdd81da38b80f02f33328af7fd4a','YzcmCZNvbXocrsz9dm8e','niefy@qq.com','16666666666',1,1,'2016-11-11 11:11:11');

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户与角色对应关系';

/*Data for the table `sys_user_role` */

/*Table structure for table `sys_user_token` */

DROP TABLE IF EXISTS `sys_user_token`;

CREATE TABLE `sys_user_token` (
  `user_id` bigint(20) NOT NULL,
  `token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'token',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE KEY `token` (`token`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='系统用户Token';

/*Data for the table `sys_user_token` */

insert  into `sys_user_token`(`user_id`,`token`,`expire_time`,`update_time`) values (1,'b24edc9724f1b047c4ec60237d6a5355','2022-04-06 08:49:21','2022-04-05 20:49:21');

/*Table structure for table `wx_account` */

DROP TABLE IF EXISTS `wx_account`;

CREATE TABLE `wx_account` (
  `appid` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'appid',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公众号名称',
  `type` tinyint(1) unsigned DEFAULT '1' COMMENT '账号类型',
  `verified` tinyint(1) unsigned DEFAULT '1' COMMENT '认证状态',
  `secret` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'appsecret',
  `token` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'token',
  `aes_key` varchar(43) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'aesKey',
  PRIMARY KEY (`appid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='公众号账号';

/*Data for the table `wx_account` */

insert  into `wx_account`(`appid`,`name`,`type`,`verified`,`secret`,`token`,`aes_key`) values ('wxfe2c610ba4fa2839','陌溪教你学编程',2,1,'49077bace79e0b11a53c1ced6eaf289d','mogu2018','');

/*Table structure for table `wx_msg` */

DROP TABLE IF EXISTS `wx_msg`;

CREATE TABLE `wx_msg` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `appid` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'appid',
  `openid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '微信用户ID',
  `in_out` tinyint(1) unsigned DEFAULT NULL COMMENT '消息方向',
  `msg_type` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '消息类型',
  `detail` json DEFAULT NULL COMMENT '消息详情',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_appid` (`appid`) USING BTREE COMMENT 'appid'
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='微信消息';

/*Data for the table `wx_msg` */

/*Table structure for table `wx_msg_reply_rule` */

DROP TABLE IF EXISTS `wx_msg_reply_rule`;

CREATE TABLE `wx_msg_reply_rule` (
  `rule_id` int(11) NOT NULL AUTO_INCREMENT,
  `appid` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT 'appid',
  `rule_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '规则名称',
  `match_value` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '匹配的关键词、事件等',
  `exact_match` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否精确匹配',
  `reply_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1' COMMENT '回复消息类型',
  `reply_content` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '回复消息内容',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '规则是否有效',
  `desc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注说明',
  `effect_time_start` time DEFAULT '00:00:00' COMMENT '生效起始时间',
  `effect_time_end` time DEFAULT '23:59:59' COMMENT '生效结束时间',
  `priority` int(3) unsigned DEFAULT '0' COMMENT '规则优先级',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`rule_id`) USING BTREE,
  KEY `idx_appid` (`appid`) USING BTREE COMMENT 'appid'
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='自动回复规则';

/*Data for the table `wx_msg_reply_rule` */

insert  into `wx_msg_reply_rule`(`rule_id`,`appid`,`rule_name`,`match_value`,`exact_match`,`reply_type`,`reply_content`,`status`,`desc`,`effect_time_start`,`effect_time_end`,`priority`,`update_time`) values (1,'','关注公众号','subscribe',0,'text','你好，欢迎关注！\n<a href=\"https://github.com/niefy\">点击链接查看我的主页</a>',1,'关注回复','00:00:00','23:59:59',0,'2020-05-20 15:15:00');

/*Table structure for table `wx_msg_template` */

DROP TABLE IF EXISTS `wx_msg_template`;

CREATE TABLE `wx_msg_template` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `appid` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'appid',
  `template_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公众号模板ID',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '模版名称',
  `title` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '模板内容',
  `data` json DEFAULT NULL COMMENT '消息内容',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '链接',
  `miniprogram` json DEFAULT NULL COMMENT '小程序信息',
  `status` tinyint(1) unsigned NOT NULL COMMENT '是否有效',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_name` (`name`) USING BTREE COMMENT '模板名称',
  KEY `idx_status` (`status`) USING BTREE COMMENT '模板状态',
  KEY `idx_appid` (`appid`) USING BTREE COMMENT 'appid'
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='消息模板';

/*Data for the table `wx_msg_template` */

/*Table structure for table `wx_qr_code` */

DROP TABLE IF EXISTS `wx_qr_code`;

CREATE TABLE `wx_qr_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `appid` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'appid',
  `is_temp` tinyint(1) DEFAULT NULL COMMENT '是否为临时二维码',
  `scene_str` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '场景值ID',
  `ticket` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '二维码ticket',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '二维码图片解析后的地址',
  `expire_time` datetime DEFAULT NULL COMMENT '该二维码失效时间',
  `create_time` datetime DEFAULT NULL COMMENT '该二维码创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_appid` (`appid`) USING BTREE COMMENT 'appid'
) ENGINE=InnoDB AUTO_INCREMENT=1511347850897707010 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='公众号带参二维码';

/*Data for the table `wx_qr_code` */

insert  into `wx_qr_code`(`id`,`appid`,`is_temp`,`scene_str`,`ticket`,`url`,`expire_time`,`create_time`) values (4,'wxfe2c610ba4fa2839',1,'推广活动','gQE48TwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyZVM1SzFBVExjNEUxajlwUDF5Y3QAAgTJjEtiAwQAjScA','http://weixin.qq.com/q/02eS5K1ATLc4E1j9pP1yct','2022-05-05 08:27:10','2022-04-05 08:27:10'),(1511346111813509121,'wxfe2c610ba4fa2839',1,'222','gQGg7zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyeGstQjF6VExjNEUxdGFyUDF5Y0EAAgRKTkxiAwQAjScA','http://weixin.qq.com/q/02xk-B1zTLc4E1tarP1ycA','2022-05-05 22:12:47','2022-04-05 22:12:47'),(1511347850897707009,'wxfe2c610ba4fa2839',1,'3333','gQEz8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyN1V3djBEVExjNEUxdkZzUDF5YzYAAgTpT0xiAwQAjScA','http://weixin.qq.com/q/027Uwv0DTLc4E1vFsP1yc6','2022-05-05 22:19:41','2022-04-05 22:19:41');

/*Table structure for table `wx_template_msg_log` */

DROP TABLE IF EXISTS `wx_template_msg_log`;

CREATE TABLE `wx_template_msg_log` (
  `log_id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `appid` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'appid',
  `touser` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户openid',
  `template_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'templateid',
  `data` json DEFAULT NULL COMMENT '消息数据',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '消息链接',
  `miniprogram` json DEFAULT NULL COMMENT '小程序信息',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `send_result` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '发送结果',
  PRIMARY KEY (`log_id`) USING BTREE,
  KEY `idx_appid` (`appid`) USING BTREE COMMENT 'appid'
) ENGINE=InnoDB AUTO_INCREMENT=116250 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='微信模版消息发送记录';

/*Data for the table `wx_template_msg_log` */

/*Table structure for table `wx_user` */

DROP TABLE IF EXISTS `wx_user`;

CREATE TABLE `wx_user` (
  `openid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '微信openid',
  `appid` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'appid',
  `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '昵称',
  `sex` tinyint(4) DEFAULT NULL COMMENT '性别(0-未知、1-男、2-女)',
  `city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '城市',
  `province` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '省份',
  `headimgurl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像',
  `subscribe_time` datetime DEFAULT NULL COMMENT '订阅时间',
  `subscribe` tinyint(3) unsigned DEFAULT '1' COMMENT '是否关注',
  `unionid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'unionid',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `tagid_list` json DEFAULT NULL COMMENT '标签ID列表',
  `subscribe_scene` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '关注场景',
  `qr_scene_str` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '扫码场景值',
  PRIMARY KEY (`openid`) USING BTREE,
  KEY `idx_unionid` (`unionid`) USING BTREE COMMENT 'unionid',
  KEY `idx_appid` (`appid`) USING BTREE COMMENT 'appid'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';

/*Data for the table `wx_user` */

insert  into `wx_user`(`openid`,`appid`,`phone`,`nickname`,`sex`,`city`,`province`,`headimgurl`,`subscribe_time`,`subscribe`,`unionid`,`remark`,`tagid_list`,`subscribe_scene`,`qr_scene_str`) values ('oNy8E6Acxr8pz-E_CrTzSKf4Z_Es','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2022-04-04 15:09:24',1,NULL,'','[]','ADD_SCENE_QR_CODE','0'),('oNy8E6Ao6gder646w_hQuK9dArpU','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-11-27 16:42:25',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6ATDWyQyTs9d5ajMUWiUap8','wxfe2c610ba4fa2839',NULL,NULL,0,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),('oNy8E6AyoyEoOxtfpTC9pNgkGBms','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-12-27 22:24:03',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6B6xRsjPYtsdZCz1FHkBqKQ','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-12-02 18:09:53',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6E3piZUW2kiGNvvNzOACTQg','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-09-30 15:47:46',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6EdVcfO0Awt8kAPs5unFrmE','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2022-03-13 11:22:10',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6Esde3PgTKGt7qlw8AEHRlo','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-11-27 20:14:51',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6FirmEBsbaHNJ5yC-IDSuD0','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-08-05 16:22:22',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6GbGeOpqWwsgeH6t1Q_w9aI','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2022-03-10 00:30:16',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6HkbLjkiWzkZoEWwPYx9wGg','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-09-28 07:26:58',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6HMMeTRtJW28xYxpos5edkY','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2022-01-12 09:38:55',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6HPJkyFIpScDv2MPxnNarPA','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-12-14 01:20:40',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6HxWISUz4n4IxmkEgdw_JQA','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2022-03-25 23:35:42',1,NULL,'','[]','ADD_SCENE_QR_CODE','0'),('oNy8E6I01SBN3pu69JNU73m6ECbk','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-06-19 09:36:29',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6J4UuZXP_z3rgaDWUVMQGnY','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-12-15 08:18:08',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6J8lII6AQA3okp-YUwDxLOQ','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2022-02-14 17:05:49',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6Ja58TgVGJRY8JYdF6T8VG4','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-07-12 09:59:27',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6JMsYhQdeKX8ZxAYeJOM1BU','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2022-01-20 15:15:22',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6KbaX9zFdRwYx8tRPoKxcn4','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-12-09 19:13:36',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6KcMimJb57n2jayzgKPr9Sg','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-12-30 09:58:04',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6KFtS_hNNjbvCZeerNa22po','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-07-14 13:46:51',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6KkkIHCFPz7uccublGQCAho','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-07-18 18:06:40',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6LbQC0bA2uadatZbcZwxMAo','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-11-11 16:45:43',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6MGaoVMPJBXlFSQzTJg5q-4','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-07-14 09:39:11',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6Nf216lvAqDqKm6XmDKNpow','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-11-14 22:19:25',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6NGbczKTQbr6_dq_CsycVHU','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-11-29 13:12:03',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6NJoXe1nD8s7P6eLD65rL34','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-09-02 10:55:13',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6NMFo3yFDl65ZqsbU9CTvNQ','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-07-15 22:23:15',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6Ny5eIt6fhM9yx-AqBHcP0M','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-07-12 14:16:02',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6OcZ3CaIOin3iRs1_OQO1ZU','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-12-12 23:26:03',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6OG2hk3d4-OERyiFIB12Psk','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-09-29 16:35:11',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6Olmu1NVtUScZTHTQDptP_I','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2022-03-15 14:18:07',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6OPPzG8wo0JLduoHnx3TaCY','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2021-10-11 14:47:45',1,NULL,'','[]','ADD_SCENE_QR_CODE','123'),('oNy8E6PQYagE0oYIrVc0afKFQBWo','wxfe2c610ba4fa2839',NULL,'',0,NULL,NULL,'','2022-03-21 23:43:29',1,NULL,'','[]','ADD_SCENE_QR_CODE','123');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
