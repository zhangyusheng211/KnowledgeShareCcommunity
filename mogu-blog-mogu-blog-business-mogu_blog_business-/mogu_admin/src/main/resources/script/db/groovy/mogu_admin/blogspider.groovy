package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/blogspider.groovy') {
    changeSet(id: '2021-12-02-t-blogspider', author: '15077731547@163.com') {
        createTable(tableName: "t_blog_spider", remarks: '博客爬取表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'title', type: 'VARCHAR(200)', remarks: '博客标题')
                    { constraints(nullable: true) }
            column(name: 'summary', type: 'VARCHAR(200)', remarks: '博客简介')
                    { constraints(nullable: true) }
            column(name: 'content', type: 'LONGTEXT', remarks: '博客内容')
                    { constraints(nullable: true) }
            column(name: 'tag_uid', type: 'VARCHAR(255)', remarks: '标签uid')
                    { constraints(nullable: true) }
            column(name: 'click_count', type: 'INT(11)', remarks: '博客点击数', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'collect_count', type: 'INT(11)', remarks: '博客收藏数', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'file_uid', type: 'VARCHAR(255)', remarks: '标题图片uid')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'admin_uid', type: 'VARCHAR(32)', remarks: '管理员uid')
                    { constraints(nullable: true) }
            column(name: 'is_original', type: 'VARCHAR(1)', remarks: '是否原创（0:不是 1：是）', defaultValue: '1')
                    { constraints(nullable: true) }
            column(name: 'author', type: 'VARCHAR(255)', remarks: '作者')
                    { constraints(nullable: true) }
            column(name: 'articles_part', type: 'VARCHAR(255)', remarks: '文章出处')
                    { constraints(nullable: true) }
            column(name: 'blog_sort_uid', type: 'VARCHAR(32)', remarks: '博客分类UID')
                    { constraints(nullable: true) }
            column(name: 'level', type: 'TINYINT(1)', remarks: '推荐等级(0:正常)', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'is_publish', type: 'VARCHAR(1)', remarks: '是否发布：0：否，1：是', defaultValue: '1')
                    { constraints(nullable: true) }
            column(name: 'sort', type: 'INT(11)', remarks: '排序字段', defaultValue: 0)
                    { constraints(nullable: false) }
        }
    }

}
