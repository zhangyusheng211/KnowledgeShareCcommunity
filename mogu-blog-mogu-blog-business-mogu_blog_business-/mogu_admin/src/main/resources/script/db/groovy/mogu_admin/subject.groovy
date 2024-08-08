package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/subject.groovy') {
    changeSet(id: '2021-12-02-t-subject', author: '15077731547@163.com') {
        createTable(tableName: "t_subject", remarks: '专题表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '主键')
                    { constraints(primaryKey: true) }
            column(name: 'subject_name', type: 'VARCHAR(255)', remarks: '专题名称')
                    { constraints(nullable: true) }
            column(name: 'summary', type: 'VARCHAR(255)', remarks: '简介')
                    { constraints(nullable: true) }
            column(name: 'file_uid', type: 'VARCHAR(32)', remarks: '封面图片UID')
                    { constraints(nullable: true) }
            column(name: 'click_count', type: 'INT(11)', remarks: '专题点击数', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'collect_count', type: 'INT(11)', remarks: '专题收藏数', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(1)', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: true) }
            column(name: 'sort', type: 'INT(11)', remarks: '排序字段', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
        }
    }

}
