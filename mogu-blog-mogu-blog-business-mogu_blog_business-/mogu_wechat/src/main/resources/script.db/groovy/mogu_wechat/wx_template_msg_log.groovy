package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/xtemplatemsglog.groovy') {
	changeSet(id: '2022-04-10-t-xtemplatemsglog', author: 'Streamlet') {
		createTable(tableName: "wx_template_msg_log", remarks: '微信模版消息发送记录') {
			column(name: 'log_id', type: 'BIGINT(11)',autoIncrement: true, remarks: 'ID')
				{ constraints(primaryKey: true) }
			column(name: 'appid', type: 'CHAR(20)',remarks: 'appid')
				{ constraints(nullable: false) }
			column(name: 'touser', type: 'VARCHAR(50)',remarks: '用户openid')
				{ constraints(nullable: true) }
			column(name: 'template_id', type: 'VARCHAR(50)',remarks: 'templateid')
				{ constraints(nullable: true) }
			column(name: 'data', type: 'JSON',remarks: '消息数据')
				{ constraints(nullable: true) }
			column(name: 'url', type: 'VARCHAR(255)',remarks: '消息链接')
				{ constraints(nullable: true) }
			column(name: 'miniprogram', type: 'JSON',remarks: '小程序信息')
				{ constraints(nullable: true) }
			column(name: 'send_time', type: 'DATETIME',remarks: '发送时间')
				{ constraints(nullable: true) }
			column(name: 'send_result', type: 'VARCHAR(255)',remarks: '发送结果')
				{ constraints(nullable: true) }
		 }

	 }

}
