package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/link.groovy') {
    changeSet(id: '2021-12-02-t-link', author: '15077731547@163.com') {
        createTable(tableName: "t_link", remarks: '友情链接表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '唯一uid')
                    { constraints(primaryKey: true) }
            column(name: 'title', type: 'VARCHAR(255)', remarks: '友情链接标题')
                    { constraints(nullable: true) }
            column(name: 'summary', type: 'VARCHAR(255)', remarks: '友情链接介绍')
                    { constraints(nullable: true) }
            column(name: 'url', type: 'VARCHAR(255)', remarks: '友情链接URL')
                    { constraints(nullable: true) }
            column(name: 'click_count', type: 'INT(11)', remarks: '点击数', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间', defaultValue: '0000-00-00 00:00:00')
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'sort', type: 'INT(11)', remarks: '排序字段，越大越靠前', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'link_status', type: 'TINYINT(1)', remarks: '友链状态： 0 申请中， 1：已上线，  2：已下架', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'user_uid', type: 'VARCHAR(32)', remarks: '申请用户UID')
                    { constraints(nullable: true) }
            column(name: 'admin_uid', type: 'VARCHAR(32)', remarks: '操作管理员UID')
                    { constraints(nullable: true) }
            column(name: 'email', type: 'VARCHAR(255)', remarks: '站长邮箱')
                    { constraints(nullable: true) }
            column(name: 'file_uid', type: 'VARCHAR(255)', remarks: '网站图标')
                    { constraints(nullable: true) }
        }
    }

}
