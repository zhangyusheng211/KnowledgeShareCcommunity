package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/userwatch.groovy') {
    changeSet(id: '2021-12-02-t-userwatch', author: '15077731547@163.com') {
        createTable(tableName: "t_user_watch", remarks: '用户关注表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '关注人UID')
                    { constraints(nullable: true) }
            column(name: 'to_user_uid', type: 'VARCHAR(32)', remarks: '被关注者UID')
                    { constraints(nullable: true) }
            column(name: 'is_admin', type: 'TINYINT(1) UNSIGNED', remarks: '是否是管理员：0否，1是', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(1) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间')
                    { constraints(nullable: true) }
        }
    }

}
