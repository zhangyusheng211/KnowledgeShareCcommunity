package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/ysmenu.groovy') {
	changeSet(id: '2022-04-10-t-ysmenu', author: 'Streamlet') {
		createTable(tableName: "sys_menu", remarks: '�˵�����') {
			column(name: 'menu_id', type: 'BIGINT(20)',autoIncrement: true, remarks: 'null')
				{ constraints(primaryKey: true) }
			column(name: 'parent_id', type: 'BIGINT(20)',remarks: '���˵�ID��һ���˵�Ϊ0')
				{ constraints(nullable: true) }
			column(name: 'name', type: 'VARCHAR(50)',remarks: '�˵�����')
				{ constraints(nullable: true) }
			column(name: 'url', type: 'VARCHAR(200)',remarks: '�˵�URL')
				{ constraints(nullable: true) }
			column(name: 'perms', type: 'VARCHAR(500)',remarks: '��Ȩ(����ö��ŷָ����磺user:list,user:create)')
				{ constraints(nullable: true) }
			column(name: 'type', type: 'INT(11)',remarks: '����   0��Ŀ¼   1���˵�   2����ť')
				{ constraints(nullable: true) }
			column(name: 'icon', type: 'VARCHAR(50)',remarks: '�˵�ͼ��')
				{ constraints(nullable: true) }
			column(name: 'order_num', type: 'INT(11)',remarks: '����')
				{ constraints(nullable: true) }
		 }

	 }

}
