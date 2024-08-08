package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/webnavbar.groovy') {
    changeSet(id: '2021-12-02-t-webnavbar', author: '15077731547@163.com') {
        createTable(tableName: "t_web_navbar", remarks: '门户页导航栏') {
            column(name: 'uid', type: 'VARCHAR(96)', remarks: '主键')
                    { constraints(nullable: true) }
            column(name: 'name', type: 'VARCHAR(765)', remarks: '导航栏名称')
                    { constraints(nullable: true) }
            column(name: 'navbar_level', type: 'TINYINT(1)', remarks: '导航栏级别')
                    { constraints(nullable: true) }
            column(name: 'summary', type: 'VARCHAR(600)', remarks: '导航栏简介')
                    { constraints(nullable: true) }
            column(name: 'parent_uid', type: 'VARCHAR(96)', remarks: '父级uid')
                    { constraints(nullable: true) }
            column(name: 'url', type: 'VARCHAR(765)', remarks: '路由')
                    { constraints(nullable: true) }
            column(name: 'icon', type: 'VARCHAR(150)', remarks: '图标')
                    { constraints(nullable: true) }
            column(name: 'is_show', type: 'TINYINT(1)', remarks: '是否显示(0: 不显示, 1: 显示)')
                    { constraints(nullable: true) }
            column(name: 'is_jump_external_url', type: 'TINYINT(1)', remarks: '是否跳转外链')
                    { constraints(nullable: true) }
            column(name: 'sort', type: 'INT(11)', remarks: '排序')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1)', remarks: '状态')
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
        }
    }

    changeSet(id: '2022-04-10-insertData-t_web_navbar', author: '陌溪') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "insert into `t_web_navbar` (`uid`, `name`, `navbar_level`, `summary`, `parent_uid`, `url`, `icon`, `is_show`, `is_jump_external_url`, `sort`, `status`, `create_time`, `update_time`) values('18118fb6fc0e5678495559e135ef754e','蘑菇圈','1','蘑菇圈',NULL,'/moment','el-icon-ice-cream','1','0','6','1','2021-12-26 16:11:25','2021-12-26 16:11:40');\n"
        }
    }

}
