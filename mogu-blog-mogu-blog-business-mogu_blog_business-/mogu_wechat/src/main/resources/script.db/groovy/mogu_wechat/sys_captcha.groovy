package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/yscaptcha.groovy') {
	changeSet(id: '2022-04-10-t-yscaptcha', author: 'Streamlet') {
		createTable(tableName: "sys_captcha", remarks: 'ϵͳ��֤��') {
			column(name: 'uuid', type: 'CHAR(36)', remarks: 'uuid')
				{ constraints(primaryKey: true) }
			column(name: 'code', type: 'VARCHAR(6)',remarks: '��֤��')
				{ constraints(nullable: false) }
			column(name: 'expire_time', type: 'DATETIME',remarks: '����ʱ��')
				{ constraints(nullable: true) }
		 }

	 }

}
