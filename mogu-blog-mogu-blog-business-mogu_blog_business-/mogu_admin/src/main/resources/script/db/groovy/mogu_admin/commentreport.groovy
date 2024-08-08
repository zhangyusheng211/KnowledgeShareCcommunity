package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/commentreport.groovy') {
    changeSet(id: '2021-12-02-t-commentreport', author: '15077731547@163.com') {
        createTable(tableName: "t_comment_report", remarks: '评论举报表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '举报人uid')
                    { constraints(nullable: true) }
            column(name: 'report_comment_uid', type: 'VARCHAR(32)', remarks: '被举报的评论Uid')
                    { constraints(nullable: true) }
            column(name: 'report_user_uid', type: 'VARCHAR(32)', remarks: '被举报的用户uid')
                    { constraints(nullable: true) }
            column(name: 'content', type: 'VARCHAR(1000)', remarks: '举报的原因')
                    { constraints(nullable: true) }
            column(name: 'progress', type: 'TINYINT(3) UNSIGNED', remarks: '进展状态: 0 未查看   1: 已查看  2：已处理', defaultValue: 1)
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
