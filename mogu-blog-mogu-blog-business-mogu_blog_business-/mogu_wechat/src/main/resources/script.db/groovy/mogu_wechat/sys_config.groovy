package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/ysconfig.groovy') {
	changeSet(id: '2022-04-10-t-ysconfig', author: 'Streamlet') {
		createTable(tableName: "sys_config", remarks: '系统配置信息表') {
			column(name: 'id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'param_key', type: 'VARCHAR(50)',remarks: 'key')
				{ constraints(nullable: true) }
			column(name: 'param_value', type: 'VARCHAR(2000)',remarks: 'value')
				{ constraints(nullable: true) }
			column(name: 'status', type: 'TINYINT(4)',remarks: '状态   0：隐藏   1：显示', defaultValue: 1)
				{ constraints(nullable: true) }
			column(name: 'remark', type: 'VARCHAR(500)',remarks: '备注')
				{ constraints(nullable: true) }
		 }

	 }

}
