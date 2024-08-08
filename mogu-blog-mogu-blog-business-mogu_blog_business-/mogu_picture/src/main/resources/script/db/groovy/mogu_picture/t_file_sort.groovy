package script.db.groovy.mogu_picture


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/filesort.groovy') {
	changeSet(id: '2021-12-04-t-filesort', author: '15077731547@163.com') {
		createTable(tableName: "t_file_sort", remarks: '文件分类表') {
			column(name: 'uid', type: 'VARCHAR(36)', remarks: '唯一uid')
				{ constraints(primaryKey: true) }
			column(name: 'project_name', type: 'VARCHAR(255)',remarks: '项目名')
				{ constraints(nullable: true) }
			column(name: 'sort_name', type: 'VARCHAR(255)',remarks: '分类名')
				{ constraints(nullable: true) }
			column(name: 'url', type: 'VARCHAR(255)',remarks: '分类路径')
				{ constraints(nullable: true) }
			column(name: 'status', type: 'TINYINT(1) UNSIGNED',remarks: '状态', defaultValue: 1)
				{ constraints(nullable: false) }
			column(name: 'create_time', type: 'TIMESTAMP',remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
				{ constraints(nullable: false) }
			column(name: 'update_time', type: 'TIMESTAMP',remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
				{ constraints(nullable: false) }
		 }

	 }

	changeSet(id: '2021-12-03-insertData-t_file_sort', author: '15077731547@163.com') {
		sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
			"insert into `t_file_sort` (`uid`, `project_name`, `sort_name`, `url`, `status`, `create_time`, `update_time`) values('a9a747d944c24845815356f72723ef8g','blog','chat','/blog/chat','1','2022-03-20 10:17:14','2022-03-20 10:17:17');\n" +
			"insert into `t_file_sort` (`uid`, `project_name`, `sort_name`, `url`, `status`, `create_time`, `update_time`) values('a9a747d944c24845815356f72723ef8h','blog','moment','/blog/moment','1','2022-03-20 10:17:14','2022-03-20 10:17:14');\n"
		}
	}

}
