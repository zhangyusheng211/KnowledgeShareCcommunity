package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/blogsort.groovy') {
    changeSet(id: '2021-12-02-t-blogsort', author: '15077731547@163.com') {
        createTable(tableName: "t_blog_sort", remarks: '博客分类表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'sort_name', type: 'VARCHAR(255)', remarks: '分类内容')
                    { constraints(nullable: true) }
            column(name: 'content', type: 'VARCHAR(255)', remarks: '分类简介')
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'sort', type: 'INT(11)', remarks: '排序字段，越大越靠前', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'click_count', type: 'INT(11)', remarks: '点击数', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'icon', type: 'VARCHAR(32)', remarks: '分类icon图标')
                    { constraints(nullable: true) }
        }
    }

}
