package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/ysuser.groovy') {
	changeSet(id: '2022-04-10-t-ysuser', author: 'Streamlet') {
		createTable(tableName: "sys_user", remarks: 'ϵͳ�û�') {
			column(name: 'user_id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'username', type: 'VARCHAR(50)',remarks: '�û���')
				{ constraints(nullable: false) }
			column(name: 'password', type: 'VARCHAR(100)',remarks: '����')
				{ constraints(nullable: true) }
			column(name: 'salt', type: 'VARCHAR(20)',remarks: '��')
				{ constraints(nullable: true) }
			column(name: 'email', type: 'VARCHAR(100)',remarks: '����')
				{ constraints(nullable: true) }
			column(name: 'mobile', type: 'VARCHAR(100)',remarks: '�ֻ���')
				{ constraints(nullable: true) }
			column(name: 'status', type: 'TINYINT(4)',remarks: '״̬  0������   1������')
				{ constraints(nullable: true) }
			column(name: 'create_user_id', type: 'BIGINT(20)',remarks: '������ID')
				{ constraints(nullable: true) }
			column(name: 'create_time', type: 'DATETIME',remarks: '����ʱ��')
				{ constraints(nullable: true) }
		 }

	 }

}
