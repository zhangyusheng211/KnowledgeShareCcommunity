package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/usermomenttopic.groovy') {
    changeSet(id: '2022-04-10-t-usermomenttopic', author: '15077731547@163.com') {
        createTable(tableName: "t_user_moment_topic", remarks: '�û������') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: 'Ψһuid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '�û�uid')
                    { constraints(nullable: true) }
            column(name: 'admin_uid', type: 'VARCHAR(32)', remarks: '����Աuid')
                    { constraints(nullable: true) }
            column(name: 'content', type: 'TEXT', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'is_publish', type: 'TINYINT(1) UNSIGNED', remarks: '�Ƿ񷢲���0����1����', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'file_uid', type: 'VARCHAR(255)', remarks: '�ļ�uid')
                    { constraints(nullable: true) }
            column(name: 'click_count', type: 'INT(11)', remarks: '�����', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'remark', type: 'VARCHAR(255)', remarks: '��ע')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1) UNSIGNED', remarks: '״̬', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'sort', type: 'INT(9)', remarks: '�����ֶ�', defaultValue: 0)
                    { constraints(nullable: false) }
        }

    }

    changeSet(id: '2022-04-10-initData-t-t_user_moment_topic', author: 'İϪ') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "insert  into `t_user_moment_topic`(`uid`,`user_uid`,`admin_uid`,`content`,`is_publish`,`file_uid`,`click_count`,`remark`,`status`,`create_time`,`update_time`,`sort`) values ('2972c039a80a68a317eff1690fbb39f7',NULL,NULL,'�°��',1,NULL,0,NULL,1,'2021-12-26 10:22:54','2021-12-26 10:22:54',0),('7f19c6130a4692ba6d32d6a2b52dc251',NULL,NULL,'СĿ��',1,NULL,0,NULL,1,'2021-12-26 10:23:16','2021-12-26 10:23:16',0),('8b16142e28a9b17af44152c37e03745b',NULL,NULL,'�����ճ�',1,NULL,0,NULL,1,'2021-12-26 10:23:10','2021-12-26 10:23:10',0),('91f62808de113aa8f8f35d43401414ba',NULL,NULL,'ְ������',1,NULL,0,NULL,1,'2021-12-26 10:23:04','2021-12-26 10:23:04',0),('9d34b3677294aad8503a6fdeeb1bf2f2',NULL,NULL,'����Ц��',1,NULL,0,NULL,1,'2021-12-26 10:23:22','2021-12-26 10:23:22',0),('d2558d4d84fc07ba64c5f18f8ec37088',NULL,NULL,'����֮��',1,'6237f22deb8e81c77e8c3a6581795e60',0,NULL,1,'2021-12-26 09:58:37','2021-12-26 10:22:26',0),('ea91f2a47f3720994b0ba10639cc7c5c',NULL,NULL,'����֮��',1,'8bf401d62306c8a4c313c70a95c22ebb',0,NULL,1,'2021-12-26 09:58:51','2021-12-26 10:22:41',0),('ed10a7820067f6bbde0957137abcf78b',NULL,NULL,'��ְ&Offer',1,NULL,0,NULL,1,'2021-12-26 10:22:48','2021-12-26 10:22:48',0);\n;"
        }
    }

}
