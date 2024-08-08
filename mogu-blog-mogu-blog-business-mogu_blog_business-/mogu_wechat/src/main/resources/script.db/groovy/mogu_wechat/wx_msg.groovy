package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/xmsg.groovy') {
	changeSet(id: '2022-04-10-t-xmsg', author: 'Streamlet') {
		createTable(tableName: "wx_msg", remarks: '΢����Ϣ') {
			column(name: 'id', type: 'BIGINT(20)',autoIncrement: true, remarks: '����')
				{ constraints(primaryKey: true) }
			column(name: 'appid', type: 'CHAR(20)',remarks: 'appid')
				{ constraints(nullable: false) }
			column(name: 'openid', type: 'VARCHAR(32)',remarks: '΢���û�ID')
				{ constraints(nullable: false) }
			column(name: 'in_out', type: 'TINYINT(1) UNSIGNED',remarks: '��Ϣ����')
				{ constraints(nullable: true) }
			column(name: 'msg_type', type: 'CHAR(25)',remarks: '��Ϣ����')
				{ constraints(nullable: true) }
			column(name: 'detail', type: 'JSON',remarks: '��Ϣ����')
				{ constraints(nullable: true) }
			column(name: 'create_time', type: 'DATETIME',remarks: '����ʱ��', defaultValue: CURRENT_TIMESTAMP)
				{ constraints(nullable: true) }
		 }

	 }

}
