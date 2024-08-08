package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/visitor.groovy') {
    changeSet(id: '2021-12-02-t-visitor', author: '15077731547@163.com') {
        createTable(tableName: "t_visitor", remarks: '游客表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_name', type: 'VARCHAR(255)', remarks: '用户名')
                    { constraints(nullable: true) }
            column(name: 'email', type: 'VARCHAR(255)', remarks: '邮箱')
                    { constraints(nullable: false) }
            column(name: 'login_count', type: 'INT(10) UNSIGNED', remarks: '登录次数', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'last_login_time', type: 'DATETIME', remarks: '最后登录时间')
                    { constraints(nullable: true) }
            column(name: 'last_login_ip', type: 'VARCHAR(50)', remarks: '最后登录IP', defaultValue: '127.0.0.1')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
        }
    }

}
