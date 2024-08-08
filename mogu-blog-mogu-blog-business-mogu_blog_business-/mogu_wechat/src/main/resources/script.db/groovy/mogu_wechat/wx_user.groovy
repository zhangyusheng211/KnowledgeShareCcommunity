package script.db.groovy.mogu_wechat


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_wechat/xuser.groovy') {
	changeSet(id: '2022-04-10-t-xuser', author: 'Streamlet') {
		createTable(tableName: "wx_user", remarks: '用户表') {
			column(name: 'openid', type: 'VARCHAR(50)', remarks: '微信openid')
				{ constraints(primaryKey: true) }
			column(name: 'appid', type: 'CHAR(20)',remarks: 'appid')
				{ constraints(nullable: false) }
			column(name: 'phone', type: 'CHAR(11)',remarks: '手机号')
				{ constraints(nullable: true) }
			column(name: 'nickname', type: 'VARCHAR(50)',remarks: '昵称')
				{ constraints(nullable: true) }
			column(name: 'sex', type: 'TINYINT(4)',remarks: '性别(0-未知、1-男、2-女)')
				{ constraints(nullable: true) }
			column(name: 'city', type: 'VARCHAR(20)',remarks: '城市')
				{ constraints(nullable: true) }
			column(name: 'province', type: 'VARCHAR(20)',remarks: '省份')
				{ constraints(nullable: true) }
			column(name: 'headimgurl', type: 'VARCHAR(255)',remarks: '头像')
				{ constraints(nullable: true) }
			column(name: 'subscribe_time', type: 'DATETIME',remarks: '订阅时间')
				{ constraints(nullable: true) }
			column(name: 'subscribe', type: 'TINYINT(3) UNSIGNED',remarks: '是否关注', defaultValue: 1)
				{ constraints(nullable: true) }
			column(name: 'unionid', type: 'VARCHAR(50)',remarks: 'unionid')
				{ constraints(nullable: true) }
			column(name: 'remark', type: 'VARCHAR(255)',remarks: '备注')
				{ constraints(nullable: true) }
			column(name: 'tagid_list', type: 'JSON',remarks: '标签ID列表')
				{ constraints(nullable: true) }
			column(name: 'subscribe_scene', type: 'VARCHAR(50)',remarks: '关注场景')
				{ constraints(nullable: true) }
			column(name: 'qr_scene_str', type: 'VARCHAR(64)',remarks: '扫码场景值')
				{ constraints(nullable: true) }
		 }

	 }

}
