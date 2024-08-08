package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/signin.groovy') {
    changeSet(id: '2021-12-02-t-signin', author: '15077731547@163.com') {
        createTable(tableName: "t_sign_in", remarks: '用户签到表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '用户uid')
                    { constraints(nullable: true) }
            column(name: 'latest_sign_date', type: 'VARCHAR(32)', remarks: '最近的签到时间')
                    { constraints(nullable: true) }
            column(name: 'consecutive_sign_count', type: 'INT(11)', remarks: '连续签到次数', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间')
                    { constraints(nullable: true) }
        }
    }

}
