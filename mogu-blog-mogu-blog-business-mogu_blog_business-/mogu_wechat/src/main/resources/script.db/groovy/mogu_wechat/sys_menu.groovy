package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/ysmenu.groovy') {
	changeSet(id: '2022-04-10-t-ysmenu', author: 'Streamlet') {
		createTable(tableName: "sys_menu", remarks: '菜单管理') {
			column(name: 'menu_id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'parent_id', type: 'BIGINT(20)',remarks: '父菜单ID，一级菜单为0')
				{ constraints(nullable: true) }
			column(name: 'name', type: 'VARCHAR(50)',remarks: '菜单名称')
				{ constraints(nullable: true) }
			column(name: 'url', type: 'VARCHAR(200)',remarks: '菜单URL')
				{ constraints(nullable: true) }
			column(name: 'perms', type: 'VARCHAR(500)',remarks: '授权(多个用逗号分隔，如：user:list,user:create)')
				{ constraints(nullable: true) }
			column(name: 'type', type: 'INT(11)',remarks: '类型   0：目录   1：菜单   2：按钮')
				{ constraints(nullable: true) }
			column(name: 'icon', type: 'VARCHAR(50)',remarks: '菜单图标')
				{ constraints(nullable: true) }
			column(name: 'order_num', type: 'INT(11)',remarks: '排序')
				{ constraints(nullable: true) }
		 }

	 }

}
