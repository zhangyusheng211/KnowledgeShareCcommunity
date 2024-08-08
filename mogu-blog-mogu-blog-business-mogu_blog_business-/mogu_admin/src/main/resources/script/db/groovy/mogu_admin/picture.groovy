package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/picture.groovy') {
    changeSet(id: '2021-12-02-t-picture', author: '15077731547@163.com') {
        createTable(tableName: "t_picture", remarks: '图片表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'file_uid', type: 'VARCHAR(32)', remarks: '图片uid')
                    { constraints(nullable: true) }
            column(name: 'pic_name', type: 'VARCHAR(255)', remarks: '图片名')
                    { constraints(nullable: true) }
            column(name: 'picture_sort_uid', type: 'VARCHAR(32)', remarks: '分类uid')
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
