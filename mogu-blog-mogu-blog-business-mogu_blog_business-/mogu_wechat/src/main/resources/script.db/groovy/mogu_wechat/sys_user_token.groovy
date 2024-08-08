package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/ysusertoken.groovy') {
	changeSet(id: '2022-04-10-t-ysusertoken', author: 'Streamlet') {
		createTable(tableName: "sys_user_token", remarks: '系统用户Token') {
			column(name: 'user_id', type: 'BIGINT(20)', remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'token', type: 'VARCHAR(100)',remarks: 'token')
				{ constraints(nullable: false) }
			column(name: 'expire_time', type: 'DATETIME',remarks: '过期时间')
				{ constraints(nullable: true) }
			column(name: 'update_time', type: 'DATETIME',remarks: '更新时间')
				{ constraints(nullable: true) }
		 }

	 }

}
