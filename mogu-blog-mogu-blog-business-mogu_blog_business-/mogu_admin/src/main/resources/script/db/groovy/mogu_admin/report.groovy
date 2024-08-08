package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/report.groovy') {
    changeSet(id: '2021-12-07-t-report', author: '15077731547@163.com') {
        createTable(tableName: "t_report", remarks: '内容举报信息') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '主键')
                    { constraints(primaryKey: true, unique: true) }
            column(name: 'report_type', type: 'VARCHAR(50)', remarks: '分类（博客/问答/评论）')
            column(name: 'report_user_uid', type: 'VARCHAR(50)', remarks: '被举报人uid')
            column(name: 'report_content_uid', type: 'VARCHAR(50)', remarks: '内容uid')
            column(name: 'user_uid', type: 'VARCHAR(50)', remarks: '举报人uid')
            column(name: 'progress', type: 'TINYINT(0) UNSIGNED', defaultValue: '0', remarks: '进展状态: 0 未处理   1: 已查看  2：已处理')
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间')
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间')
            column(name: 'violation_type', type: 'VARCHAR(255)', remarks: '违规类型')
            column(name: 'content', type: 'VARCHAR(255)', remarks: '举报内容')
        }
    }


}
