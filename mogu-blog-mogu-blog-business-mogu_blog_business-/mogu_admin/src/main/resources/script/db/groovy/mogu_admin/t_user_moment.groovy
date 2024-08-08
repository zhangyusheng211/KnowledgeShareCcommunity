package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/usermoment.groovy') {
    changeSet(id: '2022-04-10-t-usermoment', author: '15077731547@163.com') {
        createTable(tableName: "t_user_moment", remarks: '�û���̬��') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: 'Ψһuid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '�û�uid')
                    { constraints(nullable: true) }
            column(name: 'admin_uid', type: 'VARCHAR(32)', remarks: '����Աuid')
                    { constraints(nullable: true) }
            column(name: 'audit_name', type: 'VARCHAR(255)', remarks: '���������')
                    { constraints(nullable: true) }
            column(name: 'reject_reason', type: 'VARCHAR(255)', remarks: '��˾ܾ�ԭ��')
                    { constraints(nullable: true) }
            column(name: 'audit_time', type: 'DATETIME', remarks: '���ʱ��')
                    { constraints(nullable: true) }
            column(name: 'audit_status', type: 'TINYINT(1) UNSIGNED', remarks: '���״̬��0��δ��ˣ�1����˾ܾ���2�����ͨ��', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'content', type: 'TEXT', remarks: '��̬����')
                    { constraints(nullable: true) }
            column(name: 'is_publish', type: 'TINYINT(1) UNSIGNED', remarks: '�Ƿ񷢲���0����1����', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'file_uids', type: 'VARCHAR(255)', remarks: '�ļ�uid�б�')
                    { constraints(nullable: true) }
            column(name: 'topic_uids', type: 'VARCHAR(255)', remarks: '����uid�б�')
                    { constraints(nullable: true) }
            column(name: 'url', type: 'VARCHAR(255)', remarks: 'URL����')
                    { constraints(nullable: true) }
            column(name: 'url_info', type: 'VARCHAR(255)', remarks: 'URL������Ϣ')
                    { constraints(nullable: true) }
            column(name: 'click_count', type: 'INT(11)', remarks: '�����', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'comment_count', type: 'INT(11)', remarks: '������', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'remark', type: 'VARCHAR(255)', remarks: '��ע')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1) UNSIGNED', remarks: '״̬', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'open_comment', type: 'TINYINT(1)', remarks: '�Ƿ�������', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'sort', type: 'INT(11)', remarks: '�����ֶ�', defaultValue: 0)
                    { constraints(nullable: false) }
        }

    }

    changeSet(id: '2022-04-10-insertData-t_user_moment', author: 'İϪ') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "ALTER TABLE t_user_moment MODIFY COLUMN content text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '��̬����';"
        }
    }

}
