package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/collect.groovy') {
    changeSet(id: '2021-12-02-t-collect', author: '15077731547@163.com') {
        createTable(tableName: "t_collect", remarks: '收藏表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '用户的uid')
                    { constraints(nullable: false) }
            column(name: 'blog_uid', type: 'VARCHAR(32)', remarks: '博客的uid')
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
