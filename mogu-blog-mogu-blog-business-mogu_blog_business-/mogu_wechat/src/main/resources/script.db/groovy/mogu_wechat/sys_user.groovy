package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/ysuser.groovy') {
	changeSet(id: '2022-04-10-t-ysuser', author: 'Streamlet') {
		createTable(tableName: "sys_user", remarks: '系统用户') {
			column(name: 'user_id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'username', type: 'VARCHAR(50)',remarks: '用户名')
				{ constraints(nullable: false) }
			column(name: 'password', type: 'VARCHAR(100)',remarks: '密码')
				{ constraints(nullable: true) }
			column(name: 'salt', type: 'VARCHAR(20)',remarks: '盐')
				{ constraints(nullable: true) }
			column(name: 'email', type: 'VARCHAR(100)',remarks: '邮箱')
				{ constraints(nullable: true) }
			column(name: 'mobile', type: 'VARCHAR(100)',remarks: '手机号')
				{ constraints(nullable: true) }
			column(name: 'status', type: 'TINYINT(4)',remarks: '状态  0：禁用   1：正常')
				{ constraints(nullable: true) }
			column(name: 'create_user_id', type: 'BIGINT(20)',remarks: '创建者ID')
				{ constraints(nullable: true) }
			column(name: 'create_time', type: 'DATETIME',remarks: '创建时间')
				{ constraints(nullable: true) }
		 }

	 }

}
