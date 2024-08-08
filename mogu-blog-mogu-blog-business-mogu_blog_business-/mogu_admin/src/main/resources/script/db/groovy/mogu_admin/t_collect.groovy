package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/collect.groovy') {
    changeSet(id: '2022-04-10-t-collect', author: '15077731547@163.com') {
        createTable(tableName: "t_collect", remarks: '�ղر�') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: 'Ψһuid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '�û���uid')
                    { constraints(nullable: false) }
            column(name: 'collect_type', type: 'VARCHAR(3)', remarks: '�ղ����ͣ�1���ͣ�2���ʴ�')
                    { constraints(nullable: false) }
            column(name: 'biz_uid', type: 'VARCHAR(32)', remarks: 'ҵ���uid')
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '״̬', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: false) }
        }

    }

}
