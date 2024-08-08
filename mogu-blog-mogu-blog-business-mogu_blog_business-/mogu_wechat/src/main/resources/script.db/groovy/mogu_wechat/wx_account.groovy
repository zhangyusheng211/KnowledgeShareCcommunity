package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/xaccount.groovy') {
	changeSet(id: '2022-04-10-t-xaccount', author: 'Streamlet') {
		createTable(tableName: "wx_account", remarks: '���ں��˺�') {
			column(name: 'appid', type: 'CHAR(20)', remarks: 'appid')
				{ constraints(primaryKey: true) }
			column(name: 'name', type: 'VARCHAR(50)',remarks: '���ں�����')
				{ constraints(nullable: false) }
			column(name: 'type', type: 'TINYINT(1) UNSIGNED',remarks: '�˺�����', defaultValue: 1)
				{ constraints(nullable: true) }
			column(name: 'verified', type: 'TINYINT(1) UNSIGNED',remarks: '��֤״̬', defaultValue: 1)
				{ constraints(nullable: true) }
			column(name: 'secret', type: 'CHAR(32)',remarks: 'appsecret')
				{ constraints(nullable: false) }
			column(name: 'token', type: 'VARCHAR(32)',remarks: 'token')
				{ constraints(nullable: true) }
			column(name: 'aes_key', type: 'VARCHAR(43)',remarks: 'aesKey')
				{ constraints(nullable: true) }
		 }

	 }

}
