package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/ysrolemenu.groovy') {
	changeSet(id: '2022-04-10-t-ysrolemenu', author: 'Streamlet') {
		createTable(tableName: "sys_role_menu", remarks: '角色与菜单对应关系') {
			column(name: 'id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'role_id', type: 'BIGINT(20)',remarks: '角色ID')
				{ constraints(nullable: true) }
			column(name: 'menu_id', type: 'BIGINT(20)',remarks: '菜单ID')
				{ constraints(nullable: true) }
		 }

	 }

}
