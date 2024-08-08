package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/webconfig.groovy') {
    changeSet(id: '2021-12-02-t-webconfig', author: '15077731547@163.com') {
        createTable(tableName: "t_web_config", remarks: '网站配置表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '主键')
                    { constraints(primaryKey: true) }
            column(name: 'logo', type: 'VARCHAR(255)', remarks: 'logo(文件UID)')
                    { constraints(nullable: false) }
            column(name: 'name', type: 'VARCHAR(255)', remarks: '网站名称')
                    { constraints(nullable: false) }
            column(name: 'summary', type: 'VARCHAR(255)', remarks: '介绍')
                    { constraints(nullable: false) }
            column(name: 'keyword', type: 'VARCHAR(255)', remarks: '关键字')
                    { constraints(nullable: false) }
            column(name: 'author', type: 'VARCHAR(255)', remarks: '作者')
                    { constraints(nullable: false) }
            column(name: 'record_num', type: 'TEXT', remarks: '备案号')
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(1)', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间')
                    { constraints(nullable: true) }
            column(name: 'title', type: 'VARCHAR(255)', remarks: '标题')
                    { constraints(nullable: true) }
            column(name: 'ali_pay', type: 'VARCHAR(32)', remarks: '支付宝收款码FileId')
                    { constraints(nullable: true) }
            column(name: 'weixin_pay', type: 'VARCHAR(32)', remarks: '微信收款码FileId')
                    { constraints(nullable: true) }
            column(name: 'github', type: 'VARCHAR(255)', remarks: 'github地址')
                    { constraints(nullable: true) }
            column(name: 'gitee', type: 'VARCHAR(255)', remarks: 'gitee地址')
                    { constraints(nullable: true) }
            column(name: 'qq_number', type: 'VARCHAR(20)', remarks: 'QQ号')
                    { constraints(nullable: true) }
            column(name: 'qq_group', type: 'VARCHAR(20)', remarks: 'QQ群')
                    { constraints(nullable: true) }
            column(name: 'we_chat', type: 'VARCHAR(255)', remarks: '微信号')
                    { constraints(nullable: true) }
            column(name: 'email', type: 'VARCHAR(255)', remarks: '邮箱')
                    { constraints(nullable: true) }
            column(name: 'show_list', type: 'VARCHAR(255)', remarks: '显示的列表（用于控制邮箱、QQ、QQ群、Github、Gitee、微信是否显示在前端）')
                    { constraints(nullable: true) }
            column(name: 'login_type_list', type: 'VARCHAR(255)', remarks: '登录方式列表（用于控制前端登录方式，如账号密码,码云,Github,QQ,微信）')
                    { constraints(nullable: true) }
            column(name: 'open_comment', type: 'VARCHAR(1)', remarks: '是否开启评论(0:否 1:是)', defaultValue: '1')
                    { constraints(nullable: true) }
            column(name: 'open_mobile_comment', type: 'TINYINT(1)', remarks: '是否开启移动端评论(0:否， 1:是)', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'open_admiration', type: 'TINYINT(1)', remarks: '是否开启赞赏(0:否， 1:是)', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'open_mobile_admiration', type: 'TINYINT(1)', remarks: '是否开启移动端赞赏(0:否， 1:是)', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'open_create_blog', type: 'TINYINT(1)', remarks: '是否开启用户创作(0:否， 1:是)', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'link_apply_template', type: 'VARCHAR(2018)', remarks: '友链申请模板,添加友链申请模板格式')
                    { constraints(nullable: true) }
            column(name: 'open_create_question', type: 'TINYINT(1)', remarks: '是否开启问答 (0:否， 1:是)', defaultValue: 0)
                    { constraints(nullable: false) }
        }
    }

    changeSet(id: '2021-12-03-modifyColumn-t-web-config', author: '15077731547@163.com') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "ALTER TABLE t_web_config MODIFY COLUMN  NAME VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '网站名称';\n" +
                    "ALTER TABLE t_web_config MODIFY COLUMN  title VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '标题';\n" +
                    "ALTER TABLE t_web_config MODIFY COLUMN summary VARCHAR(255)  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '介绍';\n" +
                    "ALTER TABLE t_web_config MODIFY COLUMN keyword VARCHAR(255)  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '关键字';\n" +
                    "ALTER TABLE t_web_config MODIFY COLUMN author VARCHAR(255)  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '作者';\n" +
                    "ALTER TABLE t_web_config MODIFY COLUMN record_num VARCHAR(255)  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备案号';"
        }
    }

    changeSet(id: '2021-12-10-addColumn-t-web-config', author: '15077731547@163.com') {
        addColumn(tableName: 't_web_config') {
            column(name: 'open_loading_valid', type: 'tinyint(1) unsigned', defaultValue: '1', remarks: '是否开启加载校验，(0：不开启，1：开启)') {
                constraints(nullable: false)
            }
            column(name: 'loading_valid_file_uid', type: 'VARCHAR(32)', remarks: '加载校验图uid')
        }
    }
    changeSet(id: '2021-12-21-addColumn-t-web-config', author: '15077731547@163.com') {
        addColumn(tableName: 't_web_config') {
            column(name: 'loading_valid_text', type: 'TEXT', remarks: '加载校验文本')
        }
    }

    changeSet(id: '2022-04-10-insertData-t-web-config', author: '陌溪') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "UPDATE t_web_config SET chat_type_list='[\"4\",\"1\",\"3\",\"2\"]' WHERE uid = 'a331e4933cf54afcbb8c0cb11ec0830e';\n" +
                    "ALTER TABLE  t_web_config ADD chat_type_list VARCHAR(1024) COMMENT \"支持的聊天类型列表（用于控制 表情、语音、图片、通话、视频 是否显示在前端）\";\n" +
                    "ALTER TABLE  t_web_config ADD open_chat VARCHAR(1) default '1' COMMENT \"是否开启聊天\";\n" +
                    "ALTER TABLE t_web_config add wechat_file_uid varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '微信公众号二维码FileUid';"
        }
    }

}
