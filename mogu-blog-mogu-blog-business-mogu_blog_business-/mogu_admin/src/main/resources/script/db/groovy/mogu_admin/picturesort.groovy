package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/picturesort.groovy') {
    changeSet(id: '2021-12-02-t-picturesort', author: '15077731547@163.com') {
        createTable(tableName: "t_picture_sort", remarks: '图片分类表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'file_uid', type: 'VARCHAR(32)', remarks: '分类图片uid')
                    { constraints(nullable: true) }
            column(name: 'name', type: 'VARCHAR(255)', remarks: '分类名')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'parent_uid', type: 'VARCHAR(32)', remarks: 'null')
                    { constraints(nullable: true) }
            column(name: 'sort', type: 'INT(11)', remarks: '排序字段，越大越靠前', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'is_show', type: 'TINYINT(1)', remarks: '是否显示，1：是，0，否', defaultValue: 1)
                    { constraints(nullable: false) }
        }
    }

}