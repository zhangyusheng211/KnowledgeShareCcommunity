package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/mediacategory.groovy') {
    changeSet(id: '2022-04-10-t-mediacategory', author: '15077731547@163.com') {
        createTable(tableName: "t_media_category", remarks: 'ý�ʷ����') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: 'Ψһuid')
                    { constraints(primaryKey: true) }
            column(name: 'name', type: 'VARCHAR(255)', remarks: '��������')
                    { constraints(nullable: true) }
            column(name: 'content', type: 'VARCHAR(255)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '״̬')
                    { constraints(nullable: false) }
            column(name: 'sort', type: 'INT(11)', remarks: '�����ֶΣ�Խ��Խ��ǰ', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'create_by_uid', type: 'VARCHAR(32)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '����ʱ��', defaultValue: CURRENT_TIMESTAMP)
                    { constraints(nullable: true) }
            column(name: 'update_by_uid', type: 'VARCHAR(32)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'remark', type: 'VARCHAR(255)', remarks: '��ע')
                    { constraints(nullable: true) }
        }

    }

}
