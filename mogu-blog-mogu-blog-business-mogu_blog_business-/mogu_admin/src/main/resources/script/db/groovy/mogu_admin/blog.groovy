package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/blog.groovy') {
    changeSet(id: '2021-12-02-t-blog', author: '15077731547@163.com') {
        createTable(tableName: "t_blog", remarks: '博客表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'oid', type: 'INT(11)', remarks: '唯一oid')
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
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间')
                    { constraints(nullable: true) }
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
            column(name: 'open_comment', type: 'TINYINT(1)', remarks: '是否开启评论(0:否 1:是)', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'type', type: 'TINYINT(1)', remarks: '类型【0 博客， 1：推广】', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'outside_link', type: 'VARCHAR(1024)', remarks: '外链【如果是推广，那么将跳转到外链】')
                    { constraints(nullable: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '投稿用户UID')
                    { constraints(nullable: true) }
            column(name: 'article_source', type: 'TINYINT(1)', remarks: '文章来源【0 后台添加，1 用户投稿】', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'audit_status', type: 'TINYINT(1) UNSIGNED', remarks: '审批状态【0：审核未通过，1：审核通过】', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'audit_name', type: 'VARCHAR(32)', remarks: '审批人')
                    { constraints(nullable: true) }
            column(name: 'audit_time', type: 'TIMESTAMP', remarks: '审批时间')
                    { constraints(nullable: true) }
            column(name: 'reject_reason', type: 'VARCHAR(255)', remarks: '审批拒绝原因')
                    { constraints(nullable: true) }
        }
        createIndex(indexName: "idx_oid", tableName: "t_blog") {
            column(name: "oid")
        }
        addAutoIncrement(tableName: "t_blog", columnName: "oid", columnDataType: "INT(11)")

    }

    changeSet(id: '2021-12-03-modifyColumn-t-blog', author: '15077731547@163.com') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "ALTER TABLE t_blog MODIFY COLUMN title VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '博客标题';" +
                    "ALTER TABLE t_blog MODIFY COLUMN author VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '作者';" +
                    "ALTER TABLE t_blog MODIFY COLUMN summary VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '博客简介';" +
                    "ALTER TABLE t_blog MODIFY COLUMN content TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '博客内容';"
        }
    }

    changeSet(id: '2021-12-10-addColumn-t-blog', author: '15077731547@163.com') {
        addColumn(tableName: 't_blog') {
            column(name: 'open_loading_valid', type: 'tinyint(1) unsigned', defaultValue: '1', remarks: '是否开启加载校验，(0：不开启，1：开启)') {
                constraints(nullable: false)
            }
        }
    }


}
