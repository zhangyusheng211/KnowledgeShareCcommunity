package script.db.groovy.mogu_picture


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/file.groovy') {
	changeSet(id: '2021-12-04-t-file', author: '15077731547@163.com') {
		createTable(tableName: "t_file", remarks: '文件表') {
			column(name: 'uid', type: 'VARCHAR(36)', remarks: '唯一uid')
				{ constraints(primaryKey: true) }
			column(name: 'file_old_name', type: 'VARCHAR(255)',remarks: '旧文件名')
				{ constraints(nullable: true) }
			column(name: 'pic_name', type: 'VARCHAR(255)',remarks: '文件名')
				{ constraints(nullable: true) }
			column(name: 'pic_url', type: 'VARCHAR(255)',remarks: '文件地址')
				{ constraints(nullable: true) }
			column(name: 'pic_expanded_name', type: 'VARCHAR(255)',remarks: '文件扩展名')
				{ constraints(nullable: true) }
			column(name: 'file_size', type: 'INT(20)',remarks: '文件大小', defaultValue: 0)
				{ constraints(nullable: true) }
			column(name: 'file_sort_uid', type: 'VARCHAR(36)',remarks: '文件分类uid')
				{ constraints(nullable: true) }
			column(name: 'admin_uid', type: 'VARCHAR(36)',remarks: '管理员uid')
				{ constraints(nullable: true) }
			column(name: 'user_uid', type: 'VARCHAR(36)',remarks: '用户uid')
				{ constraints(nullable: true) }
			column(name: 'status', type: 'TINYINT(1) UNSIGNED',remarks: '状态', defaultValue: 1)
				{ constraints(nullable: false) }
			column(name: 'create_time', type: 'TIMESTAMP',remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
				{ constraints(nullable: false) }
			column(name: 'update_time', type: 'TIMESTAMP',remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
				{ constraints(nullable: false) }
			column(name: 'qi_niu_url', type: 'VARCHAR(255)',remarks: '七牛云地址')
				{ constraints(nullable: true) }
			column(name: 'minio_url', type: 'VARCHAR(255)',remarks: 'Minio文件URL')
				{ constraints(nullable: true) }
		 }

	 }

}
