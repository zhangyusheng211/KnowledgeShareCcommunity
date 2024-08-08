package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/xmsgtemplate.groovy') {
	changeSet(id: '2022-04-10-t-xmsgtemplate', author: 'Streamlet') {
		createTable(tableName: "wx_msg_template", remarks: '��Ϣģ��') {
			column(name: 'id', type: 'BIGINT(10) UNSIGNED',autoIncrement: true, remarks: 'id')
				{ constraints(primaryKey: true) }
			column(name: 'appid', type: 'CHAR(20)',remarks: 'appid')
				{ constraints(nullable: false) }
			column(name: 'template_id', type: 'VARCHAR(100)',remarks: '���ں�ģ��ID')
				{ constraints(nullable: false) }
			column(name: 'name', type: 'VARCHAR(50)',remarks: 'ģ������')
				{ constraints(nullable: true) }
			column(name: 'title', type: 'VARCHAR(20)',remarks: '����')
				{ constraints(nullable: true) }
			column(name: 'content', type: 'TEXT',remarks: 'ģ������')
				{ constraints(nullable: true) }
			column(name: 'data', type: 'JSON',remarks: '��Ϣ����')
				{ constraints(nullable: true) }
			column(name: 'url', type: 'VARCHAR(255)',remarks: '����')
				{ constraints(nullable: true) }
			column(name: 'miniprogram', type: 'JSON',remarks: 'С������Ϣ')
				{ constraints(nullable: true) }
			column(name: 'status', type: 'TINYINT(1) UNSIGNED',remarks: '�Ƿ���Ч')
				{ constraints(nullable: false) }
			column(name: 'update_time', type: 'DATETIME',remarks: '�޸�ʱ��', defaultValue: CURRENT_TIMESTAMP)
				{ constraints(nullable: false) }
		 }

	 }

}
