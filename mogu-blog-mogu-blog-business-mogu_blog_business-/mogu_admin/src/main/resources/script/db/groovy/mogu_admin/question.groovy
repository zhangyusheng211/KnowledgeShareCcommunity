package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/question.groovy') {
    changeSet(id: '2021-12-02-t-question', author: '15077731547@163.com') {
        createTable(tableName: "t_question", remarks: '问答表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'oid', type: 'INT(11)', remarks: '唯一oid')
                    { constraints(primaryKey: true) }
            column(name: 'title', type: 'VARCHAR(200)', remarks: '问答标题')
                    { constraints(nullable: true) }
            column(name: 'summary', type: 'VARCHAR(200)', remarks: '问答简介')
                    { constraints(nullable: true) }
            column(name: 'content', type: 'LONGTEXT', remarks: '问答内容')
                    { constraints(nullable: true) }
            column(name: 'question_tag_uid', type: 'VARCHAR(255)', remarks: '问答uid')
                    { constraints(nullable: true) }
            column(name: 'click_count', type: 'INT(11)', remarks: '问答点击数', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'collect_count', type: 'INT(11)', remarks: '问答收藏数', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'reply_count', type: 'INT(11)', remarks: '回答次数', defaultValue: 0)
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
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '用户UID')
                    { constraints(nullable: true) }
            column(name: 'is_publish', type: 'VARCHAR(1)', remarks: '是否发布：0：否，1：是', defaultValue: '1')
                    { constraints(nullable: true) }
            column(name: 'question_status', type: 'TINYINT(3) UNSIGNED', remarks: '问答状态，0:创建，1:进行，2:已采纳', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'sort', type: 'INT(11)', remarks: '排序字段', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'open_comment', type: 'TINYINT(1)', remarks: '是否开启评论(0:否 1:是)', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'question_template_uid', type: 'VARCHAR(32)', remarks: '问答模板UID')
                    { constraints(nullable: true) }
            column(name: 'question_source', type: 'TINYINT(1)', remarks: '问答来源【0 后台添加，1 用户投稿】', defaultValue: 1)
                    { constraints(nullable: false) }
        }

        createIndex(indexName: "idx_oid", tableName: "t_question") {
            column(name: "oid")
        }

        addAutoIncrement(tableName: "t_question", columnName: "oid", columnDataType: "INT(11)")

    }


    changeSet(id: '2021-12-03-modifyColumn-t-question', author: '15077731547@163.com') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "ALTER TABLE t_question MODIFY COLUMN title VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '博客标题';\n" +
                    "ALTER TABLE t_question MODIFY COLUMN summary VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '博客简介';\n" +
                    "ALTER TABLE t_question MODIFY COLUMN content TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '博客内容';"
        }
    }

}
