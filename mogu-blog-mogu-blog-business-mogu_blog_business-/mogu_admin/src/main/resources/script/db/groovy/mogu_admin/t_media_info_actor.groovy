package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/mediainfoactor.groovy') {
    changeSet(id: '2022-04-10-t-mediainfoactor', author: '15077731547@163.com') {
        createTable(tableName: "t_media_info_actor", remarks: '��Ա������') {
            column(name: 'actor_uid', type: 'VARCHAR(32)', remarks: '��Աuid')
                    { constraints(nullable: false) }
            column(name: 'media_info_uid', type: 'VARCHAR(32)', remarks: 'null')
                    { constraints(nullable: false) }
            column(name: 'type', type: 'VARCHAR(100)', remarks: '����actor  ��Ա')
                    { constraints(nullable: true) }
            column(name: 'remark', type: 'VARCHAR(255)', remarks: '��ע')
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '����')
                    { constraints(primaryKey: true) }
            column(name: 'status', type: 'TINYINT(1)', remarks: 'null')
                    { constraints(nullable: true) }
        }

    }

}
