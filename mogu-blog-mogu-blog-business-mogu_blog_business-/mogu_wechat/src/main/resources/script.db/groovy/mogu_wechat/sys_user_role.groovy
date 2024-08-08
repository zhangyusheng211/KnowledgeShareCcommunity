package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/ysuserrole.groovy') {
	changeSet(id: '2022-04-10-t-ysuserrole', author: 'Streamlet') {
		createTable(tableName: "sys_user_role", remarks: '用户与角色对应关系') {
			column(name: 'id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'user_id', type: 'BIGINT(20)',remarks: '用户ID')
				{ constraints(nullable: true) }
			column(name: 'role_id', type: 'BIGINT(20)',remarks: '角色ID')
				{ constraints(nullable: true) }
		 }

	 }

}
