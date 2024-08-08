package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/yslog.groovy') {
	changeSet(id: '2022-04-10-t-yslog', author: 'Streamlet') {
		createTable(tableName: "sys_log", remarks: 'ϵͳ��־') {
			column(name: 'id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'username', type: 'VARCHAR(50)',remarks: '�û���')
				{ constraints(nullable: true) }
			column(name: 'operation', type: 'VARCHAR(50)',remarks: '�û�����')
				{ constraints(nullable: true) }
			column(name: 'method', type: 'VARCHAR(200)',remarks: '���󷽷�')
				{ constraints(nullable: true) }
			column(name: 'params', type: 'VARCHAR(5000)',remarks: '�������')
				{ constraints(nullable: true) }
			column(name: 'time', type: 'BIGINT(20)',remarks: 'ִ��ʱ��(����)')
				{ constraints(nullable: true) }
			column(name: 'ip', type: 'VARCHAR(64)',remarks: 'IP��ַ')
				{ constraints(nullable: true) }
			column(name: 'create_date', type: 'DATETIME',remarks: '����ʱ��')
				{ constraints(nullable: true) }
		 }

	 }

}
