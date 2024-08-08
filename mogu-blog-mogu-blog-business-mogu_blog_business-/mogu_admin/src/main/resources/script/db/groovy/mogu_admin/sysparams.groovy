package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/sysparams.groovy') {
    changeSet(id: '2021-12-02-t-sysparams', author: '15077731547@163.com') {
        createTable(tableName: "t_sys_params", remarks: '参数配置表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '主键')
                    { constraints(primaryKey: true) }
            column(name: 'params_type', type: 'VARCHAR(1)', remarks: '配置类型 是否系统内置(1:，是 0:否)', defaultValue: '1')
                    { constraints(nullable: false) }
            column(name: 'params_name', type: 'VARCHAR(255)', remarks: '参数名称')
                    { constraints(nullable: true) }
            column(name: 'params_key', type: 'VARCHAR(255)', remarks: '参数键名')
                    { constraints(nullable: true) }
            column(name: 'remark', type: 'VARCHAR(255)', remarks: '备注')
                    { constraints(nullable: true) }
            column(name: 'params_value', type: 'VARCHAR(255)', remarks: '参数键值')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1)', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'sort', type: 'INT(11)', remarks: '排序字段', defaultValue: 0)
                    { constraints(nullable: false) }
        }
    }


    changeSet(id: '2022-04-10-insertData-t-sys-params', author: '陌溪') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "insert into `t_sys_params` (`uid`, `params_type`, `params_name`, `params_key`, `remark`, `params_value`, `status`, `create_time`, `update_time`, `sort`) values('5e148d83036eea64aacb1d1a67507129','1','新用户活跃限制时间','NEW_USER_LIMIT_TIME','新用户活跃（包括：评论、发表文章，发动态）限制时间（单位：小时）','3','1','2022-03-21 22:31:21','2022-03-21 22:49:47','0');\n" +
                    "INSERT INTO `t_sys_params`(`uid`, `params_type`, `params_name`, `params_key`, `remark`, `params_value`, `status`, `create_time`, `update_time`, `sort`) VALUES ('e24e6f5796a54a7eafa3507b4dc65691', '1', '面经发布限制数', 'USER_PUBLISH_PROBLEM_COUNT', '用户单日面经发布限制', '100', 1, '2022-04-02 17:32:21', '2022-04-02 17:32:21', 0);\n";
        }
    }

}
