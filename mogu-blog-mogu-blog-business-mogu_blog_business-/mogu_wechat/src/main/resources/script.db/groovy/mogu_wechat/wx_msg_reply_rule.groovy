package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/xmsgreplyrule.groovy') {
	changeSet(id: '2022-04-10-t-xmsgreplyrule', author: 'Streamlet') {
		createTable(tableName: "wx_msg_reply_rule", remarks: '�Զ��ظ�����') {
			column(name: 'rule_id', type: 'INT(11)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'appid', type: 'CHAR(20)',remarks: 'appid', defaultValue: '')
				{ constraints(nullable: true) }
			column(name: 'rule_name', type: 'VARCHAR(20)',remarks: '��������')
				{ constraints(nullable: false) }
			column(name: 'match_value', type: 'VARCHAR(200)',remarks: 'ƥ��Ĺؼ��ʡ��¼���')
				{ constraints(nullable: false) }
			column(name: 'exact_match', type: 'TINYINT(1)',remarks: '�Ƿ�ȷƥ��', defaultValue: 0)
				{ constraints(nullable: false) }
			column(name: 'reply_type', type: 'VARCHAR(20)',remarks: '�ظ���Ϣ����', defaultValue: '1')
				{ constraints(nullable: false) }
			column(name: 'reply_content', type: 'VARCHAR(1024)',remarks: '�ظ���Ϣ����')
				{ constraints(nullable: false) }
			column(name: 'status', type: 'TINYINT(1)',remarks: '�����Ƿ���Ч', defaultValue: 1)
				{ constraints(nullable: false) }
			column(name: 'desc', type: 'VARCHAR(255)',remarks: '��ע˵��')
				{ constraints(nullable: true) }
			column(name: 'effect_time_start', type: 'TIME',remarks: '��Ч��ʼʱ��', defaultValue: '00:00:00')
				{ constraints(nullable: true) }
			column(name: 'effect_time_end', type: 'TIME',remarks: '��Ч����ʱ��', defaultValue: '23:59:59')
				{ constraints(nullable: true) }
			column(name: 'priority', type: 'INT(3) UNSIGNED',remarks: '�������ȼ�', defaultValue: 0)
				{ constraints(nullable: true) }
			column(name: 'update_time', type: 'DATETIME',remarks: '�޸�ʱ��', defaultValue: CURRENT_TIMESTAMP)
				{ constraints(nullable: false) }
		 }

	 }

}
