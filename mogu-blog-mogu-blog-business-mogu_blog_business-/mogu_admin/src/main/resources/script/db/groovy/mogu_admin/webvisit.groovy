package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/webvisit.groovy') {
    changeSet(id: '2021-12-02-t-webvisit', author: '15077731547@163.com') {
        createTable(tableName: "t_web_visit", remarks: 'Web访问记录表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '主键')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(255)', remarks: '用户uid')
                    { constraints(nullable: true) }
            column(name: 'ip', type: 'VARCHAR(255)', remarks: '访问ip地址')
                    { constraints(nullable: true) }
            column(name: 'behavior', type: 'VARCHAR(255)', remarks: '用户行为')
                    { constraints(nullable: true) }
            column(name: 'module_uid', type: 'VARCHAR(255)', remarks: '模块uid（文章uid，标签uid，分类uid）')
                    { constraints(nullable: true) }
            column(name: 'other_data', type: 'VARCHAR(255)', remarks: '附加数据(比如搜索内容)')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1)', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'os', type: 'VARCHAR(255)', remarks: '操作系统')
                    { constraints(nullable: true) }
            column(name: 'browser', type: 'VARCHAR(255)', remarks: '浏览器')
                    { constraints(nullable: true) }
            column(name: 'ip_source', type: 'VARCHAR(255)', remarks: 'ip来源')
                    { constraints(nullable: true) }
        }
    }

    changeSet(id: '2022-04-10-modifyColumn-t-web-visit', author: '陌溪') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "ALTER TABLE t_web_visit MODIFY COLUMN other_data TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '附加数据(比如搜索内容)';\n" +
                    "ALTER TABLE t_web_visit ADD biz_type VARCHAR(3) NULL COMMENT '业务类型';\n"
        }
    }

}
