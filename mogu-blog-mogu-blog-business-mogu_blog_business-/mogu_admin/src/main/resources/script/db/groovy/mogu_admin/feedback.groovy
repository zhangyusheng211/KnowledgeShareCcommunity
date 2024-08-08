package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/feedback.groovy') {
    changeSet(id: '2021-12-02-t-feedback', author: '15077731547@163.com') {
        createTable(tableName: "t_feedback", remarks: '反馈表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '用户uid')
                    { constraints(nullable: false) }
            column(name: 'content', type: 'VARCHAR(1000)', remarks: '反馈的内容')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'title', type: 'VARCHAR(255)', remarks: '标题')
                    { constraints(nullable: true) }
            column(name: 'feedback_status', type: 'TINYINT(1)', remarks: '反馈状态： 0：已开启  1：进行中  2：已完成  3：已拒绝', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'reply', type: 'VARCHAR(255)', remarks: '回复')
                    { constraints(nullable: true) }
            column(name: 'admin_uid', type: 'VARCHAR(32)', remarks: '管理员uid')
                    { constraints(nullable: true) }
        }
    }

}
