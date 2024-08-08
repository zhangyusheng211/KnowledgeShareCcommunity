package script.db.groovy.mogu_picture


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/storage.groovy') {
	changeSet(id: '2021-12-04-t-storage', author: '15077731547@163.com') {
		createTable(tableName: "t_storage", remarks: '存储信息表') {
			column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
				{ constraints(primaryKey: true) }
			column(name: 'admin_uid', type: 'VARCHAR(32)',remarks: '管理员uid')
				{ constraints(nullable: false) }
			column(name: 'storage_size', type: 'BIGINT(20)',remarks: 'null')
				{ constraints(nullable: false) }
			column(name: 'status', type: 'TINYINT(1) UNSIGNED',remarks: '状态', defaultValue: 1)
				{ constraints(nullable: false) }
			column(name: 'create_time', type: 'TIMESTAMP',remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
				{ constraints(nullable: false) }
			column(name: 'update_time', type: 'TIMESTAMP',remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
				{ constraints(nullable: false) }
			column(name: 'max_storage_size', type: 'BIGINT(20)',remarks: '最大容量大小', defaultValue: 0)
				{ constraints(nullable: true) }
		 }

	 }

}
