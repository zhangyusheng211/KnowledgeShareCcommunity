package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/systemconfig.groovy') {
    changeSet(id: '2021-12-02-t-systemconfig', author: '15077731547@163.com') {
        createTable(tableName: "t_system_config", remarks: '系统配置表') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '主键')
                    { constraints(primaryKey: true) }
            column(name: 'qi_niu_access_key', type: 'VARCHAR(255)', remarks: '七牛云公钥')
                    { constraints(nullable: true) }
            column(name: 'qi_niu_secret_key', type: 'VARCHAR(255)', remarks: '七牛云私钥')
                    { constraints(nullable: true) }
            column(name: 'email', type: 'VARCHAR(255)', remarks: '邮箱账号')
                    { constraints(nullable: true) }
            column(name: 'email_user_name', type: 'VARCHAR(255)', remarks: '邮箱发件人用户名')
                    { constraints(nullable: true) }
            column(name: 'email_password', type: 'VARCHAR(255)', remarks: '邮箱密码')
                    { constraints(nullable: true) }
            column(name: 'smtp_address', type: 'VARCHAR(20)', remarks: 'SMTP地址')
                    { constraints(nullable: true) }
            column(name: 'smtp_port', type: 'VARCHAR(6)', remarks: 'SMTP端口', defaultValue: '1')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1)', remarks: '状态', defaultValue: 1)
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '创建时间')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '更新时间')
                    { constraints(nullable: true) }
            column(name: 'qi_niu_bucket', type: 'VARCHAR(255)', remarks: '七牛云上传空间')
                    { constraints(nullable: true) }
            column(name: 'qi_niu_area', type: 'VARCHAR(10)', remarks: '七牛云存储区域 华东（z0），华北(z1)，华南(z2)，北美(na0)，东南亚(as0)')
                    { constraints(nullable: true) }
            column(name: 'upload_qi_niu', type: 'VARCHAR(1)', remarks: '图片是否上传七牛云 (0:否， 1：是)', defaultValue: '1')
                    { constraints(nullable: true) }
            column(name: 'upload_local', type: 'VARCHAR(1)', remarks: '图片是否上传本地存储 (0:否， 1：是)', defaultValue: '1')
                    { constraints(nullable: true) }
            column(name: 'picture_priority', type: 'VARCHAR(1)', remarks: '图片显示优先级（ 1 展示 七牛云,  0 本地）', defaultValue: '1')
                    { constraints(nullable: true) }
            column(name: 'qi_niu_picture_base_url', type: 'VARCHAR(255)', remarks: '七牛云域名前缀：http://images.moguit.cn')
                    { constraints(nullable: true) }
            column(name: 'local_picture_base_url', type: 'VARCHAR(255)', remarks: '本地服务器域名前缀：http://localhost:8600')
                    { constraints(nullable: true) }
            column(name: 'start_email_notification', type: 'VARCHAR(1)', remarks: '是否开启邮件通知(0:否， 1:是)', defaultValue: '0')
                    { constraints(nullable: false) }
            column(name: 'editor_model', type: 'TINYINT(1)', remarks: '编辑器模式，(0：富文本编辑器CKEditor，1：markdown编辑器Veditor)', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'theme_color', type: 'VARCHAR(10)', remarks: '主题颜色', defaultValue: '#409EFF')
                    { constraints(nullable: false) }
            column(name: 'minio_end_point', type: 'VARCHAR(255)', remarks: 'Minio远程连接地址')
                    { constraints(nullable: true) }
            column(name: 'minio_access_key', type: 'VARCHAR(255)', remarks: 'Minio公钥')
                    { constraints(nullable: true) }
            column(name: 'minio_secret_key', type: 'VARCHAR(255)', remarks: 'Minio私钥')
                    { constraints(nullable: true) }
            column(name: 'minio_bucket', type: 'VARCHAR(255)', remarks: 'Minio桶')
                    { constraints(nullable: true) }
            column(name: 'upload_minio', type: 'TINYINT(1)', remarks: '图片是否上传Minio (0:否， 1：是)', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'minio_picture_base_url', type: 'VARCHAR(255)', remarks: 'Minio服务器文件域名前缀')
                    { constraints(nullable: true) }
            column(name: 'open_dashboard_notification', type: 'TINYINT(1)', remarks: '是否开启仪表盘通知(0:否， 1:是)', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'dashboard_notification', type: 'LONGTEXT', remarks: '仪表盘通知【用于首次登录弹框】')
                    { constraints(nullable: true) }
            column(name: 'content_picture_priority', type: 'TINYINT(1)', remarks: '博客详情图片显示优先级（ 0:本地  1: 七牛云 2: Minio）', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'open_email_activate', type: 'TINYINT(1)', remarks: '是否开启用户邮件激活功能【0 关闭，1 开启】', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'search_model', type: 'TINYINT(1)', remarks: '搜索模式【0:SQL搜索 、1：全文检索】', defaultValue: 0)
                    { constraints(nullable: false) }
        }
    }

}
