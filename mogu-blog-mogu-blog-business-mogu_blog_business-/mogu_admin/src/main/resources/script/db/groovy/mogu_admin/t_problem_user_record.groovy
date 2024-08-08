package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/problemuserrecord.groovy') {
    changeSet(id: '2022-04-10-t-problemuserrecord', author: '15077731547@163.com') {
        createTable(tableName: "t_problem_user_record", remarks: '�����¼��') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: 'null')
                    { constraints(primaryKey: true) }
            column(name: 'problem_uid', type: 'VARCHAR(32)', remarks: '����uid')
                    { constraints(nullable: false) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '�û�uid')
                    { constraints(nullable: true) }
            column(name: 'degree', type: 'TINYINT(3) UNSIGNED', remarks: '�������ճ̶ȣ�1��δ���ա�2��������3������', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '״̬', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
        }

    }

}
