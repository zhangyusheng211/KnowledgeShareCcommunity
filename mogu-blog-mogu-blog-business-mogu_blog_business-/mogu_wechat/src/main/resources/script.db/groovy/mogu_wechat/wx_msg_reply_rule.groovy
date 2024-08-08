package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/xmsgreplyrule.groovy') {
	changeSet(id: '2022-04-10-t-xmsgreplyrule', author: 'Streamlet') {
		createTable(tableName: "wx_msg_reply_rule", remarks: '自动回复规则') {
			column(name: 'rule_id', type: 'INT(11)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'appid', type: 'CHAR(20)',remarks: 'appid', defaultValue: '')
				{ constraints(nullable: true) }
			column(name: 'rule_name', type: 'VARCHAR(20)',remarks: '规则名称')
				{ constraints(nullable: false) }
			column(name: 'match_value', type: 'VARCHAR(200)',remarks: '匹配的关键词、事件等')
				{ constraints(nullable: false) }
			column(name: 'exact_match', type: 'TINYINT(1)',remarks: '是否精确匹配', defaultValue: 0)
				{ constraints(nullable: false) }
			column(name: 'reply_type', type: 'VARCHAR(20)',remarks: '回复消息类型', defaultValue: '1')
				{ constraints(nullable: false) }
			column(name: 'reply_content', type: 'VARCHAR(1024)',remarks: '回复消息内容')
				{ constraints(nullable: false) }
			column(name: 'status', type: 'TINYINT(1)',remarks: '规则是否有效', defaultValue: 1)
				{ constraints(nullable: false) }
			column(name: 'desc', type: 'VARCHAR(255)',remarks: '备注说明')
				{ constraints(nullable: true) }
			column(name: 'effect_time_start', type: 'TIME',remarks: '生效起始时间', defaultValue: '00:00:00')
				{ constraints(nullable: true) }
			column(name: 'effect_time_end', type: 'TIME',remarks: '生效结束时间', defaultValue: '23:59:59')
				{ constraints(nullable: true) }
			column(name: 'priority', type: 'INT(3) UNSIGNED',remarks: '规则优先级', defaultValue: 0)
				{ constraints(nullable: true) }
			column(name: 'update_time', type: 'DATETIME',remarks: '修改时间', defaultValue: CURRENT_TIMESTAMP)
				{ constraints(nullable: false) }
		 }

	 }

}
