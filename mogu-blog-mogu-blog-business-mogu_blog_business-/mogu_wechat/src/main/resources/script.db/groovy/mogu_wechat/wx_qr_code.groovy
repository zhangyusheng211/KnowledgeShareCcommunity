package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/xqrcode.groovy') {
	changeSet(id: '2022-04-10-t-xqrcode', author: 'Streamlet') {
		createTable(tableName: "wx_qr_code", remarks: '公众号带参二维码') {
			column(name: 'id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'ID')
				{ constraints(primaryKey: true) }
			column(name: 'appid', type: 'CHAR(20)',remarks: 'appid')
				{ constraints(nullable: false) }
			column(name: 'is_temp', type: 'TINYINT(1)',remarks: '是否为临时二维码')
				{ constraints(nullable: true) }
			column(name: 'scene_str', type: 'VARCHAR(64)',remarks: '场景值ID')
				{ constraints(nullable: true) }
			column(name: 'ticket', type: 'VARCHAR(255)',remarks: '二维码ticket')
				{ constraints(nullable: true) }
			column(name: 'url', type: 'VARCHAR(255)',remarks: '二维码图片解析后的地址')
				{ constraints(nullable: true) }
			column(name: 'expire_time', type: 'DATETIME',remarks: '该二维码失效时间')
				{ constraints(nullable: true) }
			column(name: 'create_time', type: 'DATETIME',remarks: '该二维码创建时间')
				{ constraints(nullable: true) }
		 }

	 }

}
