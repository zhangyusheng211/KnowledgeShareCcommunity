package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/syslog.groovy') {
    changeSet(id: '2021-12-02-t-syslog', author: '15077731547@163.com') {
        createTable(tableName: "t_sys_log", remarks: 'null') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_name', type: 'VARCHAR(255)', remarks: '用户名')
                    { constraints(nullable: false) }
            column(name: 'admin_uid', type: 'VARCHAR(32)', remarks: '管理员uid')
                    { constraints(nullable: true) }
            column(name: 'ip', type: 'VARCHAR(50)', remarks: '请求ip地址')
                    { constraints(nullable: true) }
            column(name: 'url', type: 'VARCHAR(255)', remarks: '请求url')
                    { constraints(nullable: true) }
            column(name: 'type', type: 'VARCHAR(32)', remarks: '请求方式')
                    { constraints(nullable: true) }
            column(name: 'class_path', type: 'VARCHAR(255)', remarks: '请求类路径')
                    { constraints(nullable: true) }
            column(name: 'method', type: 'VARCHAR(32)', remarks: '请求方法名')
                    { constraints(nullable: true) }
            column(name: 'params', type: 'LONGTEXT', remarks: '请求参数')
                    { constraints(nullable: true) }
            column(name: 'operation', type: 'VARCHAR(32)', remarks: '描述')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1)', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: true) }
            column(name: 'ip_source', type: 'VARCHAR(255)', remarks: 'ip来源')
                    { constraints(nullable: true) }
            column(name: 'spend_time', type: 'INT(11)', remarks: '方法请求花费的时间', defaultValue: 0)
                    { constraints(nullable: true) }
        }
    }

}
