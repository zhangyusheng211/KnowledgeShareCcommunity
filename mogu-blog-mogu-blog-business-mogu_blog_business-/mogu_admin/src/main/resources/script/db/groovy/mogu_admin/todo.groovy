package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/todo.groovy') {
    changeSet(id: '2021-12-02-t-todo', author: '15077731547@163.com') {
        createTable(tableName: "t_todo", remarks: '代办事项表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'admin_uid', type: 'VARCHAR(32)', remarks: '管理员uid')
                    { constraints(nullable: true) }
            column(name: 'text', type: 'VARCHAR(255)', remarks: '内容')
                    { constraints(nullable: true) }
            column(name: 'done', type: 'TINYINT(3) UNSIGNED', remarks: '表示事项是否完成（0：未完成 1：已完成）', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
        }
    }

}
