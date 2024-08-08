package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/questiontag.groovy') {
    changeSet(id: '2021-12-02-t-questiontag', author: '15077731547@163.com') {
        createTable(tableName: "t_question_tag", remarks: '问答标签表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'parent_uid', type: 'VARCHAR(32)', remarks: '父uid')
                    { constraints(nullable: true) }
            column(name: 'name', type: 'VARCHAR(100)', remarks: '标签名')
                    { constraints(nullable: true) }
            column(name: 'summary', type: 'VARCHAR(1000)', remarks: '标签简介')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'click_count', type: 'INT(11)', remarks: '点击数', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间')
                    { constraints(nullable: true) }
            column(name: 'sort', type: 'INT(11)', remarks: '排序字段，越大越靠前', defaultValue: 0)
                    { constraints(nullable: true) }
        }
    }

}
