package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/problemtagrelation.groovy') {
    changeSet(id: '2022-04-10-t-problemtagrelation', author: '15077731547@163.com') {
        createTable(tableName: "t_problem_tag_relation", remarks: '����-��ǩ��ϵ��') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: 'null')
                    { constraints(primaryKey: true) }
            column(name: 'problem_uid', type: 'VARCHAR(32)', remarks: 'null')
                    { constraints(nullable: true) }
            column(name: 'tag_uid', type: 'VARCHAR(32)', remarks: 'null')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '״̬', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
        }

    }

}
