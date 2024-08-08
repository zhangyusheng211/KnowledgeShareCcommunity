package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/mediaplayrecord.groovy') {
    changeSet(id: '2022-04-10-t-mediaplayrecord', author: '15077731547@163.com') {
        createTable(tableName: "t_media_play_record", remarks: '���ż�¼��') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: 'uid')
                    { constraints(primaryKey: true) }
            column(name: 'user_uid', type: 'VARCHAR(64)', remarks: '�û�id')
                    { constraints(nullable: true) }
            column(name: 'video_uid', type: 'VARCHAR(64)', remarks: '��Ƶid')
                    { constraints(nullable: true) }
            column(name: 'custom_uid', type: 'VARCHAR(64)', remarks: '�Զ���id')
                    { constraints(nullable: true) }
            column(name: 'play_duration', type: 'BIGINT(20)', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'play_position', type: 'BIGINT(20)', remarks: '��󲥷�λ��')
                    { constraints(nullable: true) }
            column(name: 'ip', type: 'VARCHAR(255)', remarks: 'ip��ַ')
                    { constraints(nullable: true) }
            column(name: 'province', type: 'VARCHAR(255)', remarks: 'ʡ������')
                    { constraints(nullable: true) }
            column(name: 'city', type: 'VARCHAR(255)', remarks: '��������')
                    { constraints(nullable: true) }
            column(name: 'referer', type: 'VARCHAR(255)', remarks: '��Դ����')
                    { constraints(nullable: true) }
            column(name: 'device', type: 'VARCHAR(255)', remarks: '�豸����')
                    { constraints(nullable: true) }
            column(name: 'operating_system', type: 'VARCHAR(255)', remarks: '����ϵͳ')
                    { constraints(nullable: true) }
            column(name: 'browser', type: 'VARCHAR(255)', remarks: '���������')
                    { constraints(nullable: true) }
            column(name: 'terminal', type: 'VARCHAR(255)', remarks: '�ն�����')
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'DATETIME', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(1)', remarks: '״̬')
                    { constraints(nullable: true) }
        }

    }

}
