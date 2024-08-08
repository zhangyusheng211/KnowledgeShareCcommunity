package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/xqrcode.groovy') {
	changeSet(id: '2022-04-10-t-xqrcode', author: 'Streamlet') {
		createTable(tableName: "wx_qr_code", remarks: '���ںŴ��ζ�ά��') {
			column(name: 'id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'ID')
				{ constraints(primaryKey: true) }
			column(name: 'appid', type: 'CHAR(20)',remarks: 'appid')
				{ constraints(nullable: false) }
			column(name: 'is_temp', type: 'TINYINT(1)',remarks: '�Ƿ�Ϊ��ʱ��ά��')
				{ constraints(nullable: true) }
			column(name: 'scene_str', type: 'VARCHAR(64)',remarks: '����ֵID')
				{ constraints(nullable: true) }
			column(name: 'ticket', type: 'VARCHAR(255)',remarks: '��ά��ticket')
				{ constraints(nullable: true) }
			column(name: 'url', type: 'VARCHAR(255)',remarks: '��ά��ͼƬ������ĵ�ַ')
				{ constraints(nullable: true) }
			column(name: 'expire_time', type: 'DATETIME',remarks: '�ö�ά��ʧЧʱ��')
				{ constraints(nullable: true) }
			column(name: 'create_time', type: 'DATETIME',remarks: '�ö�ά�봴��ʱ��')
				{ constraints(nullable: true) }
		 }

	 }

}
