package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/mediatag.groovy') {
    changeSet(id: '2022-04-10-t-mediatag', author: '15077731547@163.com') {
        createTable(tableName: "t_media_tag", remarks: 'ý�ʱ�ǩ��') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '����')
                    { constraints(primaryKey: true) }
            column(name: 'content', type: 'VARCHAR(100)', remarks: '��ǩ����')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '״̬')
                    { constraints(nullable: true) }
            column(name: 'click_count', type: 'INT(11)', remarks: '��ǩ�����', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'sort', type: 'INT(11)', remarks: '�����ֶΣ�Խ��Խ��ǰ', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'create_by_uid', type: 'VARCHAR(32)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'update_by_uid', type: 'VARCHAR(32)', remarks: '������')
                    { constraints(nullable: true) }
            column(name: 'remark', type: 'VARCHAR(255)', remarks: '��ע')
                    { constraints(nullable: true) }
            column(name: 'is_default', type: 'CHAR(1)', remarks: '�Ƿ�Ĭ�ϣ�Y�� N��')
                    { constraints(nullable: true) }
            column(name: 'css_class', type: 'VARCHAR(100)', remarks: '��ʽ���ԣ�������ʽ��չ��')
                    { constraints(nullable: true) }
            column(name: 'list_class', type: 'VARCHAR(100)', remarks: '��������ʽ')
                    { constraints(nullable: true) }
        }

    }

}
