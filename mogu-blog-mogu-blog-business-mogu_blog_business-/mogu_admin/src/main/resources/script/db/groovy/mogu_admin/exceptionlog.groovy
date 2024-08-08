package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/exceptionlog.groovy') {
    changeSet(id: '2021-12-02-t-exceptionlog', author: '15077731547@163.com') {
        createTable(tableName: "t_exception_log", remarks: 'null') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'exception_json', type: 'MEDIUMTEXT', remarks: '异常对象json格式')
                    { constraints(nullable: true) }
            column(name: 'exception_message', type: 'MEDIUMTEXT', remarks: '异常信息')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1)', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: true) }
            column(name: 'ip', type: 'VARCHAR(20)', remarks: 'ip地址')
                    { constraints(nullable: true) }
            column(name: 'ip_source', type: 'VARCHAR(100)', remarks: 'ip来源')
                    { constraints(nullable: true) }
            column(name: 'method', type: 'VARCHAR(255)', remarks: '请求方法')
                    { constraints(nullable: true) }
            column(name: 'operation', type: 'VARCHAR(100)', remarks: '方法描述')
                    { constraints(nullable: true) }
            column(name: 'params', type: 'LONGTEXT', remarks: '请求参数')
                    { constraints(nullable: true) }
        }
    }

}
