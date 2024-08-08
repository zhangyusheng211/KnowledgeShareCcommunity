package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/mediainfo.groovy') {
    changeSet(id: '2022-04-10-t-mediainfo', author: '15077731547@163.com') {
        createTable(tableName: "t_media_info", remarks: 'ý����Ϣ��') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '����')
                    { constraints(primaryKey: true) }
            column(name: 'images', type: 'VARCHAR(255)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'title', type: 'VARCHAR(255)', remarks: '����')
                    { constraints(nullable: false) }
            column(name: 'type', type: 'VARCHAR(20)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'country', type: 'VARCHAR(255)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'tag_id', type: 'VARCHAR(255)', remarks: '��ǩ')
                    { constraints(nullable: true) }
            column(name: 'description', type: 'VARCHAR(1000)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'publish_by_uid', type: 'VARCHAR(32)', remarks: '������uid')
                    { constraints(nullable: true) }
            column(name: 'publish_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1)', remarks: '״̬��0���� 1ͣ�ã�')
                    { constraints(nullable: true) }
            column(name: 'create_by_uid', type: 'VARCHAR(32)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_by_uid', type: 'VARCHAR(32)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'del_flag', type: 'CHAR(1)', remarks: 'ɾ����־��0������� 1����ɾ����')
                    { constraints(nullable: true) }
            column(name: 'remark', type: 'VARCHAR(500)', remarks: '��ע')
                    { constraints(nullable: true) }
            column(name: 'click_count', type: 'INT(11)', remarks: '�Ķ���', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'comment_count', type: 'INT(11)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'follow_count', type: 'INT(11)', remarks: '��ע��')
                    { constraints(nullable: true) }
            column(name: 'collection_count', type: 'INT(11)', remarks: '�ղ���')
                    { constraints(nullable: true) }
            column(name: 'support_count', type: 'INT(11)', remarks: '֧����')
                    { constraints(nullable: true) }
            column(name: 'oppose_count', type: 'INT(11)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'share_count', type: 'INT(11)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'open_comment', type: 'TINYINT(1)', remarks: '�Ƿ��������')
                    { constraints(nullable: true) }
            column(name: 'open_download', type: 'TINYINT(1)', remarks: '�Ƿ��������')
                    { constraints(nullable: true) }
            column(name: 'price', type: 'DECIMAL(19,2)', remarks: '�۸�')
                    { constraints(nullable: true) }
            column(name: 'rate', type: 'DOUBLE(10,1)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'category_uid', type: 'VARCHAR(32)', remarks: '����uid')
                    { constraints(nullable: true) }
            column(name: 'category_name', type: 'VARCHAR(255)', remarks: '��������')
                    { constraints(nullable: true) }
            column(name: 'qrcode_path', type: 'VARCHAR(255)', remarks: '��Ӱר����ά��')
                    { constraints(nullable: true) }
            column(name: 'open_password', type: 'TINYINT(1)', remarks: '�Ƿ�����ά�� 0 �ر� 1����')
                    { constraints(nullable: true) }
            column(name: 'password', type: 'VARCHAR(50)', remarks: '����˽�ܷ���ʱ����Կ')
                    { constraints(nullable: true) }
            column(name: 'stills', type: 'VARCHAR(1000)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'total_video_length', type: 'BIGINT(20)', remarks: '��Ƶ�ܳ���')
                    { constraints(nullable: true) }
            column(name: 'summary', type: 'VARCHAR(800)', remarks: '���')
                    { constraints(nullable: true) }
            column(name: 'en', type: 'VARCHAR(100)', remarks: 'ƴ��')
                    { constraints(nullable: true) }
            column(name: 'letter', type: 'VARCHAR(100)', remarks: '����ĸ��д')
                    { constraints(nullable: true) }
            column(name: 'lang', type: 'VARCHAR(30)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'sort', type: 'INT(9) UNSIGNED', remarks: '�����ֶ�', defaultValue: 1)
                    { constraints(nullable: false) }
        }

    }

}
