package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/questiontemplate.groovy') {
    changeSet(id: '2021-12-02-t-questiontemplate', author: '15077731547@163.com') {
        createTable(tableName: "t_question_template", remarks: '问答表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'name', type: 'VARCHAR(200)', remarks: '模板名称')
                    { constraints(nullable: true) }
            column(name: 'summary', type: 'VARCHAR(200)', remarks: '模板简介')
                    { constraints(nullable: true) }
            column(name: 'content', type: 'LONGTEXT', remarks: '模板内容')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间')
                    { constraints(nullable: true) }
            column(name: 'is_publish', type: 'VARCHAR(1)', remarks: '是否发布：0：否，1：是', defaultValue: '1')
                    { constraints(nullable: true) }
            column(name: 'sort', type: 'INT(11)', remarks: '排序字段', defaultValue: 0)
                    { constraints(nullable: false) }
        }
    }

}
