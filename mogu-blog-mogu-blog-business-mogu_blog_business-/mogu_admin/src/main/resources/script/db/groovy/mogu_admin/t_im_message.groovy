package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/immessage.groovy') {
    changeSet(id: '2022-04-10-t-immessage', author: '15077731547@163.com') {
        createTable(tableName: "t_im_message", remarks: '�����¼��') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: 'Ψһuid')
                    { constraints(primaryKey: true) }
            column(name: 'room_id', type: 'VARCHAR(32)', remarks: '�����һỰid')
                    { constraints(nullable: true) }
            column(name: 'from_user_id', type: 'VARCHAR(32)', remarks: '��Ϣ������')
                    { constraints(nullable: true) }
            column(name: 'from_user_nick_name', type: 'VARCHAR(255)', remarks: '�������ǳ�')
                    { constraints(nullable: true) }
            column(name: 'form_user_avatar_uid', type: 'VARCHAR(255)', remarks: '������ͷ��')
                    { constraints(nullable: true) }
            column(name: 'message_level', type: 'INT(5)', remarks: '��Ϣ����(������Ϣ/����/֪ͨ/����)')
                    { constraints(nullable: true) }
            column(name: 'message', type: 'TEXT', remarks: '��Ϣ����')
                    { constraints(nullable: true) }
            column(name: 'send_time', type: 'DATETIME', remarks: '��Ϣ����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'category', type: 'INT(5)', remarks: '��Ϣ���(�ı���Ϣ��������Ϣ��ͼƬ��Ϣ)')
                    { constraints(nullable: true) }
            column(name: 'duration', type: 'INT(10)', remarks: '��������(��)')
                    { constraints(nullable: true) }
            column(name: 'is_read', type: 'TINYINT(1)', remarks: '�Ѷ�', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'to_user_id', type: 'VARCHAR(32)', remarks: '��Ϣ������')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '״̬', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'message_type', type: 'INT(5)', remarks: '��Ϣ���� ˽��/Ⱥ��/��ִ')
                    { constraints(nullable: true) }
            column(name: 'operator_type', type: 'INT(11)', remarks: '��������(�ѽ���/�ܽ�/��ȡ��/δ����)')
                    { constraints(nullable: true) }
        }

    }

}
