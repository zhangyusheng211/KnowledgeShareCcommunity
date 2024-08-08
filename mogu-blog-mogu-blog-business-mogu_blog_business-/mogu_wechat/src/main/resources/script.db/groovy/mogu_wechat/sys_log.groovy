package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/yslog.groovy') {
	changeSet(id: '2022-04-10-t-yslog', author: 'Streamlet') {
		createTable(tableName: "sys_log", remarks: '系统日志') {
			column(name: 'id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'username', type: 'VARCHAR(50)',remarks: '用户名')
				{ constraints(nullable: true) }
			column(name: 'operation', type: 'VARCHAR(50)',remarks: '用户操作')
				{ constraints(nullable: true) }
			column(name: 'method', type: 'VARCHAR(200)',remarks: '请求方法')
				{ constraints(nullable: true) }
			column(name: 'params', type: 'VARCHAR(5000)',remarks: '请求参数')
				{ constraints(nullable: true) }
			column(name: 'time', type: 'BIGINT(20)',remarks: '执行时长(毫秒)')
				{ constraints(nullable: true) }
			column(name: 'ip', type: 'VARCHAR(64)',remarks: 'IP地址')
				{ constraints(nullable: true) }
			column(name: 'create_date', type: 'DATETIME',remarks: '创建时间')
				{ constraints(nullable: true) }
		 }

	 }

}
