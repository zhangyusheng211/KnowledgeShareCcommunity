package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/xuser.groovy') {
	changeSet(id: '2022-04-10-t-xuser', author: 'Streamlet') {
		createTable(tableName: "wx_user", remarks: '�û���') {
			column(name: 'openid', type: 'VARCHAR(50)', remarks: '΢��openid')
				{ constraints(primaryKey: true) }
			column(name: 'appid', type: 'CHAR(20)',remarks: 'appid')
				{ constraints(nullable: false) }
			column(name: 'phone', type: 'CHAR(11)',remarks: '�ֻ���')
				{ constraints(nullable: true) }
			column(name: 'nickname', type: 'VARCHAR(50)',remarks: '�ǳ�')
				{ constraints(nullable: true) }
			column(name: 'sex', type: 'TINYINT(4)',remarks: '�Ա�(0-δ֪��1-�С�2-Ů)')
				{ constraints(nullable: true) }
			column(name: 'city', type: 'VARCHAR(20)',remarks: '����')
				{ constraints(nullable: true) }
			column(name: 'province', type: 'VARCHAR(20)',remarks: 'ʡ��')
				{ constraints(nullable: true) }
			column(name: 'headimgurl', type: 'VARCHAR(255)',remarks: 'ͷ��')
				{ constraints(nullable: true) }
			column(name: 'subscribe_time', type: 'DATETIME',remarks: '����ʱ��')
				{ constraints(nullable: true) }
			column(name: 'subscribe', type: 'TINYINT(3) UNSIGNED',remarks: '�Ƿ��ע', defaultValue: 1)
				{ constraints(nullable: true) }
			column(name: 'unionid', type: 'VARCHAR(50)',remarks: 'unionid')
				{ constraints(nullable: true) }
			column(name: 'remark', type: 'VARCHAR(255)',remarks: '��ע')
				{ constraints(nullable: true) }
			column(name: 'tagid_list', type: 'JSON',remarks: '��ǩID�б�')
				{ constraints(nullable: true) }
			column(name: 'subscribe_scene', type: 'VARCHAR(50)',remarks: '��ע����')
				{ constraints(nullable: true) }
			column(name: 'qr_scene_str', type: 'VARCHAR(64)',remarks: 'ɨ�볡��ֵ')
				{ constraints(nullable: true) }
		 }

	 }

}
