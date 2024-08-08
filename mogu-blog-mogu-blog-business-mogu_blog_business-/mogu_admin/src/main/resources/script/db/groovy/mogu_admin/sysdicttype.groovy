package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/sysdicttype.groovy') {
    changeSet(id: '2021-12-02-t-sysdicttype', author: '15077731547@163.com') {
        createTable(tableName: "t_sys_dict_type", remarks: '字典类型表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '主键')
                    { constraints(primaryKey: true) }
            column(name: 'oid', type: 'INT(11)', remarks: '自增键oid')
                    { constraints(nullable: false) }
            column(name: 'dict_name', type: 'VARCHAR(255)', remarks: '字典名称')
                    { constraints(nullable: true) }
            column(name: 'dict_type', type: 'VARCHAR(255)', remarks: '字典类型')
                    { constraints(nullable: true) }
            column(name: 'create_by_uid', type: 'VARCHAR(32)', remarks: '创建人UID')
                    { constraints(nullable: false) }
            column(name: 'update_by_uid', type: 'VARCHAR(32)', remarks: '最后更新人UID')
                    { constraints(nullable: false) }
            column(name: 'remark', type: 'VARCHAR(255)', remarks: '备注')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1)', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'is_publish', type: 'VARCHAR(1)', remarks: '是否发布(1:是，0:否)', defaultValue: '1')
                    { constraints(nullable: false) }
            column(name: 'sort', type: 'INT(11)', remarks: '排序字段', defaultValue: 0)
                    { constraints(nullable: false) }
        }

        createIndex(indexName: "idx_oid", tableName: "t_sys_dict_type") {
            column(name: "oid")
        }

        addAutoIncrement(tableName: "t_sys_dict_type", columnName: "oid", columnDataType: "INT(11)")


    }

    changeSet(id: '2022-04-10-insert-t-sys_dict_type', author: '陌溪') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "INSERT INTO `t_sys_dict_type`(`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) VALUES ('2bb2299f9324edbf38e5ac3cf4a8a265', 39, 'media_video_storage_type', 'media_video_storage_type', '1f01cd1d2f474743b241d74008b12333', '1f01cd1d2f474743b241d74008b12333', 'media_video_storage_type', 1, '2022-01-17 19:04:01', '2022-01-17 19:04:01', '1', 0);\n" +
                    "insert into `t_sys_dict_type` (`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) values('15d9c45bb8cca6549cd0889760c77d06','44','是否精选','sys_selection','1f01cd1d2f474743b241d74008b12333','1f01cd1d2f474743b241d74008b12333','是否精选','1','2022-03-10 22:27:03','2022-03-10 22:27:03','1','0');\n" +
                    "insert into `t_sys_dict_type` (`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) values('3d9496b946d3230de0e08e65f6ccab3d','43','标签等级','sys_tag_level','1f01cd1d2f474743b241d74008b12333','1f01cd1d2f474743b241d74008b12333','标签等级','1','2022-03-09 21:39:35','2022-03-09 21:39:40','1','0');\n" +
                    "insert into `t_sys_dict_type` (`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) values('9be390a549d541c4b16e4ffe960afbab','42','问题类型','sys_problem_type','1f01cd1d2f474743b241d74008b12333','1f01cd1d2f474743b241d74008b12333','问题类型','1','2022-03-08 08:44:26','2022-03-08 08:44:26','1','0');\n" +
                    "insert into `t_sys_dict_type` (`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) values('7e8f7eac894ac2f8bd816e4f9501b687','41','问题难度','sys_problem_difficulty','1f01cd1d2f474743b241d74008b12333','1f01cd1d2f474743b241d74008b12333','问题难度','1','2022-03-08 08:44:10','2022-03-08 08:44:10','1','0');\n" +
                    "insert into `t_sys_dict_type` (`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) values('95d42152a689342aec8bfd646e8cd5ae','40','问题标签类型','sys_problem_tag_type','1f01cd1d2f474743b241d74008b12333','1f01cd1d2f474743b241d74008b12333','问题标签类型','1','2022-03-07 23:44:30','2022-03-07 23:47:15','1','0');\n" +
                    "insert into `t_sys_dict_type` (`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) values('85096ec6932a94918ea8bda576fc57a9','47','聊天类别','sys_message_category','1f01cd1d2f474743b241d74008b12333','1f01cd1d2f474743b241d74008b12333','聊天类别','1','2022-03-19 16:24:42','2022-03-19 16:24:42','1','0');\n" +
                    "insert into `t_sys_dict_type` (`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) values('607d5932ce14260474faad549eb996d5','46','聊天消息类型','sys_message_type','1f01cd1d2f474743b241d74008b12333','1f01cd1d2f474743b241d74008b12333','聊天消息类型','1','2022-03-19 16:21:11','2022-03-19 16:21:23','1','0');\n" +
                    "insert into `t_sys_dict_type` (`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) values('7b866788143c93541e71f4a29da1579b','45','聊天消息级别','sys_message_level','1f01cd1d2f474743b241d74008b12333','1f01cd1d2f474743b241d74008b12333','聊天消息级别','1','2022-03-19 15:47:14','2022-03-19 15:47:30','1','0');\n" +
                    "INSERT INTO `t_sys_dict_type`(`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) VALUES ('fef03e6a511b6ddf533a91b2e818e219', 38, '语言', 'lang', '1f01cd1d2f474743b241d74008b12333', '1f01cd1d2f474743b241d74008b12333', '语言', 1, '2021-12-16 22:10:35', '2021-12-16 22:10:35', '1', 0);\n" +
                    "INSERT INTO `t_sys_dict_type`(`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) VALUES ('4f919b583611d14057a98a2101fd7fce', 37, '演员标签', 'actor_label', '1f01cd1d2f474743b241d74008b12333', '1f01cd1d2f474743b241d74008b12333', '演员标签', 1, '2021-12-16 21:07:48', '2021-12-16 21:07:48', '1', 0);\n" +
                    "INSERT INTO `t_sys_dict_type`(`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) VALUES ('7cb2bbba02dfd05abeadedd66d7cbe7c', 36, '媒资类型', 'media_type', '1f01cd1d2f474743b241d74008b12333', '1f01cd1d2f474743b241d74008b12333', '媒资类型', 1, '2021-12-14 22:42:49', '2021-12-14 22:42:49', '1', 0);\n" +
                    "INSERT INTO `t_sys_dict_type`(`uid`, `oid`, `dict_name`, `dict_type`, `create_by_uid`, `update_by_uid`, `remark`, `status`, `create_time`, `update_time`, `is_publish`, `sort`) VALUES ('f550e45ae76d8cb786fb0b8b1fb34c51', 35, '国家', 'media_country', '1f01cd1d2f474743b241d74008b12333', '1f01cd1d2f474743b241d74008b12333', '国家', 1, '2021-12-14 22:40:04', '2021-12-14 22:40:04', '1', 0);\n"
        }
    }

}
