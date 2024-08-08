package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/problem.groovy') {
    changeSet(id: '2022-04-10-t-problem', author: '15077731547@163.com') {
        createTable(tableName: "t_problem", remarks: '�����') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: 'Ψһuid')
                    { constraints(primaryKey: true) }
            column(name: 'oid', type: 'INT(11)', autoIncrement: true, remarks: 'Ψһoid')
                    { constraints(primaryKey: true) }
            column(name: 'title', type: 'VARCHAR(200)', remarks: '�������')
                    { constraints(nullable: true) }
            column(name: 'summary', type: 'VARCHAR(200)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'content', type: 'LONGTEXT', remarks: '��������')
                    { constraints(nullable: true) }
            column(name: 'answer', type: 'LONGTEXT', remarks: '�����׼��')
                    { constraints(nullable: true) }
            column(name: 'problem_difficulty', type: 'TINYINT(3) UNSIGNED', remarks: '�����Ѷ�(0��Ĭ�ϣ�1: �򵥣�2���еȣ�3������)', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'problem_type', type: 'TINYINT(3) UNSIGNED', remarks: '��������(0��Ĭ��, 1: ����⣬2��ѡ���⣬3�������4: ��գ�5�������)', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'click_count', type: 'INT(11)', remarks: '��������', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'collect_count', type: 'INT(11)', remarks: '�����ղ���', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'comment_count', type: 'INT(11)', remarks: '���۴���', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'pass_count', type: 'INT(11)', remarks: '����ͨ����', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'no_pass_count', type: 'INT(11)', remarks: '����δͨ����', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'visit_count', type: 'INT(11)', remarks: '�����г��ֵĴ���', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'file_uid', type: 'VARCHAR(255)', remarks: '����ͼƬuid')
                    { constraints(nullable: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '�û�UID')
                    { constraints(nullable: true) }
            column(name: 'sort', type: 'INT(11)', remarks: '�����ֶ�', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'open_comment', type: 'TINYINT(1)', remarks: '�Ƿ�������(0:�� 1:��)', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'admin_uid', type: 'VARCHAR(32)', remarks: '����Աuid')
                    { constraints(nullable: true) }
            column(name: 'audit_status', type: 'TINYINT(3)', remarks: '�Ƿ�ͨ����ˣ�0����1���ǣ�', defaultValue: 1)
                    { constraints(nullable: true) }
            column(name: 'audit_name', type: 'VARCHAR(255)', remarks: '�����')
                    { constraints(nullable: true) }
            column(name: 'audit_time', type: 'TIMESTAMP', remarks: '���ʱ��')
                    { constraints(nullable: true) }
            column(name: 'is_selection', type: 'TINYINT(3) UNSIGNED', remarks: '�Ƿ�ѡ', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'is_publish', type: 'TINYINT(3) UNSIGNED', remarks: '�Ƿ��ϼ�', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'publish_time', type: 'TIMESTAMP', remarks: '�ϼ�ʱ��')
                    { constraints(nullable: true) }
            column(name: 'source', type: 'TINYINT(3)', remarks: '��Դ', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '״̬', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: false) }
            column(name: 'problem_tag_uid', type: 'VARCHAR(300)', remarks: '�����ǩuid�б�')
                    { constraints(nullable: true) }
            column(name: 'has_answer', type: 'VARCHAR(3)', remarks: '�Ƿ��д�', defaultValue: '1')
                    { constraints(nullable: true) }
            column(name: 'reject_reason', type: 'VARCHAR(255)', remarks: '�����ܾ�ԭ��')
                    { constraints(nullable: true) }
        }

    }

    changeSet(id: '2022-04-10-insert-t-problem', author: 'İϪ') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "insert  into `t_problem_tag`(`uid`,`parent_uid`,`name`,`summary`,`tag_type`,`is_selection`,`is_publish`,`status`,`click_count`,`create_time`,`update_time`,`sort`,`tag_level`,`is_jump_external_url`,`url`,`icon`) values ('0f06446523567b97719abcb9e7e86cb8','25b5e02f3c55a2e21fe30d6b26b0f12a','Python',NULL,1,1,1,1,0,'2022-03-10 21:48:05','2022-03-10 21:48:19',0,2,0,NULL,'el-icon-document-copy'),('1be75f1aa7de54019c007116788acde6','25b5e02f3c55a2e21fe30d6b26b0f12a','C#',NULL,1,1,1,1,0,'2022-03-10 21:49:25','2022-03-10 21:49:25',0,2,0,NULL,'el-icon-suitcase'),('25b5e02f3c55a2e21fe30d6b26b0f12a','78e5872643cf7f20e8cf3bf0a6a546bd','�������','����',1,1,1,1,0,'2022-03-09 22:26:23','2022-03-12 13:08:40',0,1,0,'','el-icon-hot-water'),('29322984a2856dc50fabf841ba8f1429','25b5e02f3c55a2e21fe30d6b26b0f12a','Spring','����2',1,1,1,1,0,'2022-03-08 00:07:15','2022-03-10 21:47:29',0,2,0,NULL,'el-icon-monitor'),('2dd493646f4003ee5b33543e41103374','c1dffd546ccf1b9b4b74b1bbf1b9ad72','�����з�',NULL,3,1,1,1,0,'2022-03-12 13:01:35','2022-03-12 13:01:35',0,2,0,NULL,'el-icon-guide'),('339342fac35a330d8b245f4833a470bc','25b5e02f3c55a2e21fe30d6b26b0f12a','SpringCloud',NULL,1,1,1,1,0,'2022-03-12 12:57:14','2022-03-12 12:57:14',0,2,0,NULL,'el-icon-monitor'),('344d7548aedd359739ac349f66845cce',NULL,'��˾',NULL,5,1,1,1,0,'2022-03-10 08:40:30','2022-03-12 13:08:49',0,1,0,'','el-icon-setting'),('5a1f71f3474cb91c0350b2cf2780b2b8','c1dffd546ccf1b9b4b74b1bbf1b9ad72','���',NULL,3,1,1,1,0,'2022-03-11 22:57:00','2022-03-11 22:57:13',0,2,0,NULL,'el-icon-brush'),('62140acb480e891a2960d6d688701fe2',NULL,'ѧ��֪ʶ','123123',5,1,1,1,0,'2022-03-09 22:25:45','2022-03-10 08:36:41',0,1,0,NULL,'el-icon-camera'),('78e5872643cf7f20e8cf3bf0a6a546bd',NULL,'����','���',2,1,0,0,0,'2022-03-07 23:42:57','2022-03-09 22:51:26',0,1,0,NULL,NULL),('8222c32ed152174ec7507baa505f5ae6','25b5e02f3c55a2e21fe30d6b26b0f12a','JavaScript',NULL,1,1,1,1,0,'2022-03-12 12:58:19','2022-03-12 12:58:19',0,2,0,NULL,'el-icon-brush'),('b071f7e171659b9d00933713ae9d6358','25b5e02f3c55a2e21fe30d6b26b0f12a','Java',NULL,1,1,1,1,0,'2022-03-10 21:46:26','2022-03-10 21:46:26',0,2,0,NULL,'el-icon-headset'),('b1f54f51afa684e291cb891d28e4b293','25b5e02f3c55a2e21fe30d6b26b0f12a','C++',NULL,1,1,1,1,0,'2022-03-10 21:49:01','2022-03-10 21:49:01',0,2,0,NULL,'el-icon-umbrella'),('be2dc47825ac385b3169fa723429b33b','c1dffd546ccf1b9b4b74b1bbf1b9ad72','�ͻ���',NULL,3,1,1,1,0,'2022-03-12 13:00:11','2022-03-12 13:00:11',0,2,0,NULL,'el-icon-reading'),('bfcd9405bf0fbe56602a95e4a11a479c','25b5e02f3c55a2e21fe30d6b26b0f12a','C',NULL,1,1,1,1,0,'2022-03-10 21:49:45','2022-03-10 21:49:45',0,2,0,NULL,'el-icon-paperclip'),('c1dffd546ccf1b9b4b74b1bbf1b9ad72',NULL,'������',NULL,3,1,1,1,0,'2022-03-10 21:51:02','2022-03-10 21:51:22',0,1,0,NULL,'el-icon-guide'),('ca1472462c08aab2ef6557e5c0b40480','c1dffd546ccf1b9b4b74b1bbf1b9ad72','�㷨',NULL,3,1,1,1,0,'2022-03-12 13:00:36','2022-03-12 13:00:36',0,2,0,NULL,'el-icon-shopping-cart-full'),('d47f466bfac3e9a2c72effbc45a88826','25b5e02f3c55a2e21fe30d6b26b0f12a','Html',NULL,1,1,1,1,0,'2022-03-12 12:58:47','2022-03-12 12:58:47',0,2,0,NULL,'el-icon-mouse'),('d797ddcad3d42d8e8f3eee738a175f47','25b5e02f3c55a2e21fe30d6b26b0f12a','SpringBoot',NULL,1,1,1,1,0,'2022-03-12 12:56:41','2022-03-12 12:56:41',0,2,0,NULL,'el-icon-umbrella'),('dec59884b4fa25a222242437779d5dec','25b5e02f3c55a2e21fe30d6b26b0f12a','Golang',NULL,1,1,1,1,0,'2022-03-10 21:48:40','2022-03-10 21:48:40',0,2,0,NULL,'el-icon-coordinate'),('df73fcbfca44d203fb3b861ae98f83c7','c1dffd546ccf1b9b4b74b1bbf1b9ad72','ǰ��',NULL,3,1,1,1,0,'2022-03-12 12:59:28','2022-03-12 13:02:49',0,2,0,NULL,'el-icon-data-analysis'),('f8c9bcf5ab7d4b221bc95d68b92578bf','c1dffd546ccf1b9b4b74b1bbf1b9ad72','����',NULL,3,1,1,1,0,'2022-03-12 13:01:01','2022-03-12 13:01:01',0,2,0,NULL,'el-icon-shopping-bag-2');\n"
        }
    }

}
