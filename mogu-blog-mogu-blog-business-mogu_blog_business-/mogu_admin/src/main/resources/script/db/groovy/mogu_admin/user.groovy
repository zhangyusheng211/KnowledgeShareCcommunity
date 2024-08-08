package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/user.groovy') {
    changeSet(id: '2021-12-02-t-user', author: '15077731547@163.com') {
        createTable(tableName: "t_user", remarks: '用户表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_name', type: 'VARCHAR(255)', remarks: '用户名')
                    { constraints(nullable: false) }
            column(name: 'pass_word', type: 'VARCHAR(32)', remarks: '密码')
                    { constraints(nullable: false) }
            column(name: 'gender', type: 'TINYINT(3) UNSIGNED', remarks: '性别(1:男2:女)')
                    { constraints(nullable: true) }
            column(name: 'avatar', type: 'VARCHAR(100)', remarks: '个人头像')
                    { constraints(nullable: true) }
            column(name: 'email', type: 'VARCHAR(60)', remarks: '邮箱')
                    { constraints(nullable: true) }
            column(name: 'birthday', type: 'DATE', remarks: '出生年月日')
                    { constraints(nullable: true) }
            column(name: 'mobile', type: 'VARCHAR(50)', remarks: '手机')
                    { constraints(nullable: true) }
            column(name: 'valid_code', type: 'VARCHAR(255)', remarks: '邮箱验证码')
                    { constraints(nullable: true) }
            column(name: 'summary', type: 'VARCHAR(200)', remarks: '自我简介最多150字')
                    { constraints(nullable: true) }
            column(name: 'login_count', type: 'INT(10) UNSIGNED', remarks: '登录次数', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'last_login_time', type: 'DATETIME', remarks: '最后登录时间')
                    { constraints(nullable: true) }
            column(name: 'last_login_ip', type: 'VARCHAR(50)', remarks: '最后登录IP', defaultValue: '127.0.0.1')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'nick_name', type: 'VARCHAR(255)', remarks: '昵称')
                    { constraints(nullable: true) }
            column(name: 'source', type: 'VARCHAR(255)', remarks: '资料来源')
                    { constraints(nullable: true) }
            column(name: 'uuid', type: 'VARCHAR(255)', remarks: '平台uuid')
                    { constraints(nullable: true) }
            column(name: 'qq_number', type: 'VARCHAR(20)', remarks: 'QQ号')
                    { constraints(nullable: true) }
            column(name: 'we_chat', type: 'VARCHAR(255)', remarks: '微信号')
                    { constraints(nullable: true) }
            column(name: 'occupation', type: 'VARCHAR(255)', remarks: '职业')
                    { constraints(nullable: true) }
            column(name: 'comment_status', type: 'TINYINT(1)', remarks: '评论状态 1:正常 0:禁言', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'ip_source', type: 'VARCHAR(255)', remarks: 'ip来源')
                    { constraints(nullable: true) }
            column(name: 'browser', type: 'VARCHAR(255)', remarks: '浏览器')
                    { constraints(nullable: true) }
            column(name: 'os', type: 'VARCHAR(255)', remarks: '操作系统')
                    { constraints(nullable: true) }
            column(name: 'start_email_notification', type: 'TINYINT(1)', remarks: '是否开启邮件通知 1:开启 0:关闭', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'user_tag', type: 'TINYINT(1)', remarks: '用户标签：0：普通用户，1：管理员，2：博主 等', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'loading_valid', type: 'TINYINT(1)', remarks: '是否通过加载校验【0 未通过，1 已通过】', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'credits', type: 'INT(11)', remarks: '积分', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'background_file_uid', type: 'VARCHAR(32)', remarks: '个人中心背景图片')
                    { constraints(nullable: true) }
        }
    }

    changeSet(id: '2021-12-03-modifyColumn-t-user', author: '15077731547@163.com') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "ALTER TABLE t_user MODIFY COLUMN occupation VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '职业';\n" +
                    "ALTER TABLE t_user MODIFY COLUMN summary VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '自我简介最多150字';"
        }
    }
    changeSet(id: '2021-12-21-addColumn-t-user', author: '15077731547@163.com') {
        addColumn(tableName: 't_user') {
            column(name: 'editor_model', type: 'tinyint(1) unsigned', remarks: '编辑器模式，(0：富文本编辑器CKEditor，1：markdown编辑器Veditor)') {
                constraints(nullable: false)
            }
        }
    }

}
