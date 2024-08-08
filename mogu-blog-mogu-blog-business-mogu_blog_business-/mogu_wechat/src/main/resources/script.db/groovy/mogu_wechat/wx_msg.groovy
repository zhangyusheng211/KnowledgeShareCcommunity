package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/xmsg.groovy') {
	changeSet(id: '2022-04-10-t-xmsg', author: 'Streamlet') {
		createTable(tableName: "wx_msg", remarks: '微信消息') {
			column(name: 'id', type: 'BIGINT(20)',autoIncrement: true, remarks: '主键')
				{ constraints(primaryKey: true) }
			column(name: 'appid', type: 'CHAR(20)',remarks: 'appid')
				{ constraints(nullable: false) }
			column(name: 'openid', type: 'VARCHAR(32)',remarks: '微信用户ID')
				{ constraints(nullable: false) }
			column(name: 'in_out', type: 'TINYINT(1) UNSIGNED',remarks: '消息方向')
				{ constraints(nullable: true) }
			column(name: 'msg_type', type: 'CHAR(25)',remarks: '消息类型')
				{ constraints(nullable: true) }
			column(name: 'detail', type: 'JSON',remarks: '消息详情')
				{ constraints(nullable: true) }
			column(name: 'create_time', type: 'DATETIME',remarks: '创建时间', defaultValue: CURRENT_TIMESTAMP)
				{ constraints(nullable: true) }
		 }

	 }

}
