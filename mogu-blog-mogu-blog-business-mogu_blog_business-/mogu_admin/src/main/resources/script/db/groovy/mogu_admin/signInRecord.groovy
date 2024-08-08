package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/signInRecord.groovy') {
    changeSet(id: '2021-12-21-t_sign_in_record', author: '15077731547@163.com') {
        createTable(tableName: "t_sign_in_record", remarks: '用户签到表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '用户uid')
            column(name: 'sign_date', type: 'VARCHAR(32)', remarks: '签到时间')
            column(name: 'sign_type', type: 'tinyint(1)', remarks: '签到类型(0:每日签到，1:补签)')
            column(name: 'status', type: 'tinyint(0) UNSIGNED', defaultValue: 1, remarks: '状态')
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间')
                    { constraints(nullable: true) }
        }
        addUniqueConstraint(tableName: 't_sign_in_record', constraintName: 'user_uid_sign_date', columnNames: 'user_uid,sign_date')

    }


}
