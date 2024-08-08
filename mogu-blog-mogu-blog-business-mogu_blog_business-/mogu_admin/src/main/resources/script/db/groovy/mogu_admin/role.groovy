package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/role.groovy') {
    changeSet(id: '2021-12-02-t-role', author: '15077731547@163.com') {
        createTable(tableName: "t_role", remarks: 'null') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '角色id')
                    { constraints(primaryKey: true) }
            column(name: 'role_name', type: 'VARCHAR(255)', remarks: '角色名')
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'summary', type: 'VARCHAR(255)', remarks: '角色介绍')
                    { constraints(nullable: true) }
            column(name: 'category_menu_uids', type: 'TEXT', remarks: '角色管辖的菜单的UID')
                    { constraints(nullable: true) }
        }
    }

}
