package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/creditslog.groovy') {
    changeSet(id: '2021-12-02-t-creditslog', author: '15077731547@163.com') {
        createTable(tableName: "t_credits_log", remarks: '用户签到表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'admin_uid', type: 'VARCHAR(32)', remarks: '管理员uid')
                    { constraints(nullable: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '用户uid')
                    { constraints(nullable: true) }
            column(name: 'user_name', type: 'VARCHAR(255)', remarks: '用户uid')
                    { constraints(nullable: true) }
            column(name: 'resource_uid', type: 'VARCHAR(32)', remarks: '操作的对象uid')
                    { constraints(nullable: true) }
            column(name: 'action_code', type: 'VARCHAR(32)', remarks: '动作码')
                    { constraints(nullable: true) }
            column(name: 'action_name', type: 'VARCHAR(32)', remarks: '动作名称')
                    { constraints(nullable: true) }
            column(name: 'old_credits', type: 'INT(11)', remarks: '旧的积分数', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'new_credits', type: 'INT(11)', remarks: '新的积分数', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'remark', type: 'VARCHAR(255)', remarks: '备注')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间')
                    { constraints(nullable: true) }
        }
    }

    changeSet(id: '2021-12-18-addColumn-t-credits-log', author: '15077731547@163.com') {
        addColumn(tableName: 't_credits_log') {
            column(name: 'change_credits', type: 'int(11)', defaultValue: '0', remarks: '变更的积分')
        }
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "update t_credits_log set change_credits = new_credits - old_credits;"
        }
    }


}
