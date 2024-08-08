package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/ysoss.groovy') {
	changeSet(id: '2022-04-10-t-ysoss', author: 'Streamlet') {
		createTable(tableName: "sys_oss", remarks: '�ļ��ϴ�') {
			column(name: 'id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'url', type: 'VARCHAR(200)',remarks: 'URL��ַ')
				{ constraints(nullable: true) }
			column(name: 'create_date', type: 'DATETIME',remarks: '����ʱ��')
				{ constraints(nullable: true) }
		 }

	 }

}
