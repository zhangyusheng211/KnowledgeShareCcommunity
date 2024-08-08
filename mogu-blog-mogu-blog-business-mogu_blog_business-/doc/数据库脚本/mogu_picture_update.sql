DROP TABLE IF EXISTS `t_network_disk`;

CREATE TABLE `t_network_disk` (
  `uid` varchar(32) NOT NULL COMMENT '唯一uid',
  `admin_uid` varchar(32) NOT NULL COMMENT '管理员uid',
  `extend_name` varchar(255) DEFAULT NULL COMMENT '扩展名',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名',
  `file_path` varchar(255) DEFAULT NULL COMMENT '文件路径',
  `file_size` bigint(20) NOT NULL COMMENT '文件大小',
  `is_dir` int(11) NOT NULL COMMENT '是否目录',
  `status` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `local_url` varchar(255) DEFAULT NULL COMMENT '本地文件URL',
  `qi_niu_url` varchar(255) DEFAULT NULL COMMENT '七牛云URL',
  `file_old_name` varchar(255) DEFAULT NULL COMMENT '上传前文件名',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='网盘文件表';


DROP TABLE IF EXISTS `t_storage`;

CREATE TABLE `t_storage` (
  `uid` varchar(32) NOT NULL COMMENT '唯一uid',
  `admin_uid` varchar(32) NOT NULL COMMENT '管理员uid',
  `storage_size` bigint(20) NOT NULL DEFAULT 0 COMMENT '网盘容量大小',
  `status` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='存储信息表';


/*
 在t_storage表，添加最大容量限制字段
 @date 2020年10月9日09:55:28
*/
alter table t_storage add max_storage_size bigint default 0 null comment '最大容量大小';



/*
 在t_file 表，添加Minio文件URL字段
 @date 2020年10月21日21:18:11
*/
ALTER TABLE  t_file ADD minio_url varchar(255) default NULL COMMENT "Minio文件URL";

ALTER TABLE  t_network_disk ADD minio_url varchar(255) default NULL COMMENT "Minio文件URL";


/*
 新增两个项目空间
 @date 2022年3月20日20:38:05
*/
insert into `t_file_sort` (`uid`, `project_name`, `sort_name`, `url`, `status`, `create_time`, `update_time`) values('a9a747d944c24845815356f72723ef8g','blog','chat','/blog/chat','1','2022-03-20 10:17:14','2022-03-20 10:17:17');
insert into `t_file_sort` (`uid`, `project_name`, `sort_name`, `url`, `status`, `create_time`, `update_time`) values('a9a747d944c24845815356f72723ef8h','blog','moment','/blog/moment','1','2022-03-20 10:17:14','2022-03-20 10:17:14');

/**
用于删除阿里oss文件
 */
ALTER TABLE t_network_disk ADD ali_oss_url VARCHAR(255) NULL COMMENT "阿里云文件url";
ALTER TABLE t_file ADD ali_oss_url VARCHAR(255) NULL COMMENT "阿里云文件url";



/*
* 文件md5值
*/
ALTER TABLE t_file ADD file_md5 varchar(255) COMMENT '文件md5值';
ALTER TABLE t_file ADD INDEX index_file_md5(file_md5);


/*
 新增m3u8文件地址
 @date 2024年1月8日23:31:36
*/
ALTER TABLE t_file ADD m3u8_url varchar(1024) COMMENT 'm3u8文件地址';
ALTER TABLE t_file ADD video_length varchar(1024) COMMENT '媒资长度';