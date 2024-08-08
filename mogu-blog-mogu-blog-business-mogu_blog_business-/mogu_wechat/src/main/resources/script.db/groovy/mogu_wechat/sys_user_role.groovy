package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/ysuserrole.groovy') {
	changeSet(id: '2022-04-10-t-ysuserrole', author: 'Streamlet') {
		createTable(tableName: "sys_user_role", remarks: '�û����ɫ��Ӧ��ϵ') {
			column(name: 'id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'user_id', type: 'BIGINT(20)',remarks: '�û�ID')
				{ constraints(nullable: true) }
			column(name: 'role_id', type: 'BIGINT(20)',remarks: '��ɫID')
				{ constraints(nullable: true) }
		 }

	 }

}
