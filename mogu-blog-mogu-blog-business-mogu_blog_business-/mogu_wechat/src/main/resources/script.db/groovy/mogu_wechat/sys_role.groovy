package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/ysrole.groovy') {
	changeSet(id: '2022-04-10-t-ysrole', author: 'Streamlet') {
		createTable(tableName: "sys_role", remarks: '��ɫ') {
			column(name: 'role_id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'role_name', type: 'VARCHAR(100)',remarks: '��ɫ����')
				{ constraints(nullable: true) }
			column(name: 'remark', type: 'VARCHAR(100)',remarks: '��ע')
				{ constraints(nullable: true) }
			column(name: 'create_user_id', type: 'BIGINT(20)',remarks: '������ID')
				{ constraints(nullable: true) }
			column(name: 'create_time', type: 'DATETIME',remarks: '����ʱ��')
				{ constraints(nullable: true) }
		 }

	 }

}
