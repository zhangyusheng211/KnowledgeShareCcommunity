package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/userpraiserecord.groovy') {
    changeSet(id: '2022-04-10-t-userpraiserecord', author: '15077731547@163.com') {
        createTable(tableName: "t_user_praise_record", remarks: '�û����ޱ�') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '���޼�¼uid')
                    { constraints(primaryKey: true) }
            column(name: 'resource_type', type: 'VARCHAR(255)', remarks: '��Դ����')
                    { constraints(nullable: true) }
            column(name: 'resource_uid', type: 'VARCHAR(32)', remarks: '��Դuid')
                    { constraints(nullable: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '�û�uid')
                    { constraints(nullable: true) }
            column(name: 'remark', type: 'VARCHAR(255)', remarks: '��ע')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '״̬', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'praise_type', type: 'INT(11)', remarks: '��������, ĿǰĬ��Ϊ1 ����  0 ���', defaultValue: 1)
                    { constraints(nullable: true) }
        }

    }

}
