package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/mediaactor.groovy') {
    changeSet(id: '2022-04-10-t-mediaactor', author: '15077731547@163.com') {
        createTable(tableName: "t_media_actor", remarks: '��Ա��') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '����')
                    { constraints(primaryKey: true) }
            column(name: 'name', type: 'VARCHAR(50)', remarks: '����')
                    { constraints(nullable: false) }
            column(name: 'avatar', type: 'VARCHAR(255)', remarks: 'ͷ��')
                    { constraints(nullable: true) }
            column(name: 'description', type: 'VARCHAR(1000)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'awards', type: 'VARCHAR(255)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'label', type: 'VARCHAR(100)', remarks: '��ǩ ')
                    { constraints(nullable: true) }
            column(name: 'create_by_uid', type: 'VARCHAR(32)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_by_uid', type: 'VARCHAR(32)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'remark', type: 'VARCHAR(255)', remarks: '��ע')
                    { constraints(nullable: true) }
            column(name: 'collect_count', type: 'INT(11)', remarks: '�ղ���')
                    { constraints(nullable: true) }
            column(name: 'click_count', type: 'INT(11)', remarks: '�����')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1)', remarks: 'null')
                    { constraints(nullable: true) }
        }

    }

}
