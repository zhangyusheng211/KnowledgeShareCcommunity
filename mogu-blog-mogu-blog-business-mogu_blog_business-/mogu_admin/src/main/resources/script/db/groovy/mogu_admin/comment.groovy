package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/comment.groovy') {
    changeSet(id: '2021-12-02-t-comment', author: '15077731547@163.com') {
        createTable(tableName: "t_comment", remarks: '评论表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '用户uid')
                    { constraints(nullable: true) }
            column(name: 'to_uid', type: 'VARCHAR(32)', remarks: '回复某条评论的uid')
                    { constraints(nullable: true) }
            column(name: 'to_user_uid', type: 'VARCHAR(32)', remarks: '回复某个人的uid')
                    { constraints(nullable: true) }
            column(name: 'content', type: 'VARCHAR(2048)', remarks: '评论内容')
                    { constraints(nullable: true) }
            column(name: 'blog_uid', type: 'VARCHAR(32)', remarks: '博客uid')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'source', type: 'VARCHAR(255)', remarks: '评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等')
                    { constraints(nullable: false) }
            column(name: 'TYPE', type: 'TINYINT(1)', remarks: '评论类型 1:点赞 0:评论', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'first_comment_uid', type: 'VARCHAR(32)', remarks: '一级评论UID')
                    { constraints(nullable: true) }
        }
    }

}
