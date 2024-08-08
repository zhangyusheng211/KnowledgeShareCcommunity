package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/studyvideo.groovy') {
    changeSet(id: '2021-12-02-t-studyvideo', author: '15077731547@163.com') {
        createTable(tableName: "t_study_video", remarks: '学习视频表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'file_uid', type: 'VARCHAR(32)', remarks: '视频封面图片uid')
                    { constraints(nullable: true) }
            column(name: 'resource_sort_uid', type: 'VARCHAR(255)', remarks: '资源分类UID')
                    { constraints(nullable: true) }
            column(name: 'name', type: 'VARCHAR(255)', remarks: '视频名称')
                    { constraints(nullable: true) }
            column(name: 'summary', type: 'VARCHAR(255)', remarks: '视频简介')
                    { constraints(nullable: true) }
            column(name: 'content', type: 'VARCHAR(255)', remarks: '分类介绍')
                    { constraints(nullable: true) }
            column(name: 'baidu_path', type: 'VARCHAR(255)', remarks: '百度云完整路径')
                    { constraints(nullable: true) }
            column(name: 'click_count', type: 'VARCHAR(255)', remarks: '点击数')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'parent_uid', type: 'VARCHAR(32)', remarks: 'null')
                    { constraints(nullable: true) }
        }
    }

}
