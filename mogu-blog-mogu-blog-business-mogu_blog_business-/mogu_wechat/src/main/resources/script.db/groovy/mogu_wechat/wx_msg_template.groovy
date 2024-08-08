package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/xmsgtemplate.groovy') {
	changeSet(id: '2022-04-10-t-xmsgtemplate', author: 'Streamlet') {
		createTable(tableName: "wx_msg_template", remarks: '消息模板') {
			column(name: 'id', type: 'BIGINT(10) UNSIGNED',autoIncrement: true, remarks: 'id')
				{ constraints(primaryKey: true) }
			column(name: 'appid', type: 'CHAR(20)',remarks: 'appid')
				{ constraints(nullable: false) }
			column(name: 'template_id', type: 'VARCHAR(100)',remarks: '公众号模板ID')
				{ constraints(nullable: false) }
			column(name: 'name', type: 'VARCHAR(50)',remarks: '模版名称')
				{ constraints(nullable: true) }
			column(name: 'title', type: 'VARCHAR(20)',remarks: '标题')
				{ constraints(nullable: true) }
			column(name: 'content', type: 'TEXT',remarks: '模板内容')
				{ constraints(nullable: true) }
			column(name: 'data', type: 'JSON',remarks: '消息内容')
				{ constraints(nullable: true) }
			column(name: 'url', type: 'VARCHAR(255)',remarks: '链接')
				{ constraints(nullable: true) }
			column(name: 'miniprogram', type: 'JSON',remarks: '小程序信息')
				{ constraints(nullable: true) }
			column(name: 'status', type: 'TINYINT(1) UNSIGNED',remarks: '是否有效')
				{ constraints(nullable: false) }
			column(name: 'update_time', type: 'DATETIME',remarks: '修改时间', defaultValue: CURRENT_TIMESTAMP)
				{ constraints(nullable: false) }
		 }

	 }

}
