package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/userEquityRecord.groovy') {
    changeSet(id: '2021-12-21-t_user_equity_record', author: '15077731547@163.com') {
        createTable(tableName: "t_user_equity_record", remarks: '用户权益记录表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '用户uid')
            column(name: 'equity_uid', type: 'VARCHAR(32)', remarks: '权益uid')
            column(name: 'equity_type', type: 'tinyint(1)', remarks: '权益类型(1:VIP特权, 2:签到卡, 3:兑换的资源)')
            column(name: 'is_permanent', type: 'tinyint(1)', defaultValue: 0, remarks: '是否永久生效（0：否，1：是）')
            column(name: 'use_status', type: 'tinyint(1)', defaultValue: 0, remarks: '使用状态（0：未使用，1：已使用，2：已过期）')
            column(name: 'start_time', type: 'TIMESTAMP', remarks: '权益生效时间')
            column(name: 'end_time', type: 'TIMESTAMP', remarks: '权益截止时间')
            column(name: 'status', type: 'tinyint(0) UNSIGNED', defaultValue: 1, remarks: '状态')
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间')
                    { constraints(nullable: true) }
        }
        createIndex(indexName: "index_user_status", tableName: "t_user_equity_record") {
            column(name: "user_uid")
            column(name: "use_status")
        }

    }


}
