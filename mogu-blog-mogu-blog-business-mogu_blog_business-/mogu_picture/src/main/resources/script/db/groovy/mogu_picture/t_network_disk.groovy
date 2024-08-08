package script.db.groovy.mogu_picture


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/networkdisk.groovy') {
	changeSet(id: '2021-12-04-t-networkdisk', author: '15077731547@163.com') {
		createTable(tableName: "t_network_disk", remarks: '网盘文件表') {
			column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
				{ constraints(primaryKey: true) }
			column(name: 'admin_uid', type: 'VARCHAR(32)',remarks: '管理员uid')
				{ constraints(nullable: false) }
			column(name: 'extend_name', type: 'VARCHAR(255)',remarks: '扩展名')
				{ constraints(nullable: true) }
			column(name: 'file_name', type: 'VARCHAR(255)',remarks: '文件名')
				{ constraints(nullable: true) }
			column(name: 'file_path', type: 'VARCHAR(255)',remarks: '文件路径')
				{ constraints(nullable: true) }
			column(name: 'file_size', type: 'BIGINT(20)',remarks: '文件大小')
				{ constraints(nullable: false) }
			column(name: 'is_dir', type: 'INT(11)',remarks: '是否目录')
				{ constraints(nullable: false) }
			column(name: 'status', type: 'TINYINT(1) UNSIGNED',remarks: '状态', defaultValue: 1)
				{ constraints(nullable: false) }
			column(name: 'create_time', type: 'TIMESTAMP',remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
				{ constraints(nullable: false) }
			column(name: 'update_time', type: 'TIMESTAMP',remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
				{ constraints(nullable: false) }
			column(name: 'local_url', type: 'VARCHAR(255)',remarks: '本地文件URL')
				{ constraints(nullable: true) }
			column(name: 'qi_niu_url', type: 'VARCHAR(255)',remarks: '七牛云URL')
				{ constraints(nullable: true) }
			column(name: 'file_old_name', type: 'VARCHAR(255)',remarks: '上传前文件名')
				{ constraints(nullable: true) }
			column(name: 'minio_url', type: 'VARCHAR(255)',remarks: 'Minio文件URL')
				{ constraints(nullable: true) }
		 }

	 }

}
