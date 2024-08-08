package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/admin.groovy') {
    changeSet(id: '2021-12-02-t-admin', author: '15077731547@163.com') {
        createTable(tableName: "t_admin", remarks: '管理员表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_name', type: 'VARCHAR(255)', remarks: '用户名')
                    { constraints(nullable: false) }
            column(name: 'pass_word', type: 'VARCHAR(255)', remarks: '密码')
                    { constraints(nullable: false) }
            column(name: 'gender', type: 'VARCHAR(1)', remarks: '性别(1:男2:女)')
                    { constraints(nullable: true) }
            column(name: 'avatar', type: 'VARCHAR(100)', remarks: '个人头像')
                    { constraints(nullable: true) }
            column(name: 'email', type: 'VARCHAR(60)', remarks: '邮箱')
                    { constraints(nullable: true) }
            column(name: 'birthday', type: 'DATE', remarks: '出生年月日')
                    { constraints(nullable: true) }
            column(name: 'mobile', type: 'VARCHAR(11)', remarks: '手机')
                    { constraints(nullable: true) }
            column(name: 'valid_code', type: 'VARCHAR(50)', remarks: '邮箱验证码')
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
            column(name: 'qq_number', type: 'VARCHAR(255)', remarks: 'QQ号')
                    { constraints(nullable: true) }
            column(name: 'we_chat', type: 'VARCHAR(255)', remarks: '微信号')
                    { constraints(nullable: true) }
            column(name: 'occupation', type: 'VARCHAR(255)', remarks: '职业')
                    { constraints(nullable: true) }
            column(name: 'github', type: 'VARCHAR(255)', remarks: 'github地址')
                    { constraints(nullable: true) }
            column(name: 'gitee', type: 'VARCHAR(255)', remarks: 'gitee地址')
                    { constraints(nullable: true) }
            column(name: 'role_uid', type: 'VARCHAR(32)', remarks: '拥有的角色uid')
                    { constraints(nullable: true) }
            column(name: 'person_resume', type: 'TEXT', remarks: '履历')
                    { constraints(nullable: true) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '管理员绑定的用户【绑定的用户后，将以该用户的名义进行创作】')
                    { constraints(nullable: true) }
        }
    }

    changeSet(id: '2021-12-03-modifyColumn-t-admin', author: '15077731547@163.com') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "ALTER TABLE t_admin MODIFY COLUMN nick_name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '昵称';\n" +
                    "ALTER TABLE t_admin MODIFY COLUMN occupation VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '职业';\n" +
                    "ALTER TABLE t_admin MODIFY COLUMN summary VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '自我简介最多150字';"
        }
    }

}
