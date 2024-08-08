package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/mediavideo.groovy') {
    changeSet(id: '2022-04-10-t-mediavideo', author: '15077731547@163.com') {
        createTable(tableName: "t_media_video", remarks: 'ý����Ƶ��') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '����')
                    { constraints(primaryKey: true) }
            column(name: 'title', type: 'VARCHAR(100)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'url', type: 'VARCHAR(255)', remarks: 'url��ַ')
                    { constraints(nullable: true) }
            column(name: 'ext', type: 'VARCHAR(255)', remarks: '�ļ���׺')
                    { constraints(nullable: true) }
            column(name: 'length', type: 'VARCHAR(20)', remarks: '���ų���')
                    { constraints(nullable: true) }
            column(name: 'type', type: 'VARCHAR(255)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'storage_type', type: 'VARCHAR(255)', remarks: '�洢����')
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'create_by_uid', type: 'VARCHAR(32)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_by_uid', type: 'VARCHAR(32)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'del_flag', type: 'CHAR(1)', remarks: 'ɾ����־')
                    { constraints(nullable: true) }
            column(name: 'remark', type: 'VARCHAR(500)', remarks: '��ע')
                    { constraints(nullable: true) }
            column(name: 'media_info_uid', type: 'VARCHAR(32)', remarks: '��Ӱid')
                    { constraints(nullable: true) }
            column(name: 'filesize', type: 'BIGINT(20)', remarks: '�ļ���С�ֽ���')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1)', remarks: '״̬1 ��ת��  2 ����ת�� 3ת��ʧ�� 4 ת���ɹ�')
                    { constraints(nullable: true) }
            column(name: 'super_definition_url', type: 'VARCHAR(255)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'high_definition_url', type: 'VARCHAR(255)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'standard_definition_url', type: 'VARCHAR(255)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'error_msg', type: 'VARCHAR(1000)', remarks: 'ת��������Ϣ')
                    { constraints(nullable: true) }
        }

    }

}
