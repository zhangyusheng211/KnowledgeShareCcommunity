package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/notice.groovy') {
    changeSet(id: '2021-12-02-t-notice', author: '15077731547@163.com') {
        createTable(tableName: "t_notice", remarks: '通知表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '用户uid')
                    { constraints(nullable: true) }
            column(name: 'admin_uid', type: 'VARCHAR(32)', remarks: '管理员uid')
                    { constraints(nullable: true) }
            column(name: 'notice_status', type: 'TINYINT(1) UNSIGNED', remarks: '通知状态：0:已创建，1:已查看', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'notice_type', type: 'TINYINT(1) UNSIGNED', remarks: '通知类型', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'content', type: 'TEXT', remarks: '通知内容')
                    { constraints(nullable: true) }
            column(name: 'business_uid', type: 'VARCHAR(32)', remarks: '业务uid')
                    { constraints(nullable: true) }
            column(name: 'business_type', type: 'TINYINT(1) UNSIGNED', remarks: '业务类型 【博客，问答，评论】', defaultValue: 0)
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
