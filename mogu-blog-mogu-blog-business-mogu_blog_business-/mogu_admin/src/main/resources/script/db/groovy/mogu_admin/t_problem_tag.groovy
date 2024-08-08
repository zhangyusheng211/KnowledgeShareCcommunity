package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/problemtag.groovy') {
    changeSet(id: '2022-04-10-t-problemtag', author: '15077731547@163.com') {
        createTable(tableName: "t_problem_tag", remarks: '�����ǩ��') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: 'Ψһuid')
                    { constraints(primaryKey: true) }
            column(name: 'parent_uid', type: 'VARCHAR(32)', remarks: '��uid')
                    { constraints(nullable: true) }
            column(name: 'name', type: 'VARCHAR(100)', remarks: '��ǩ��')
                    { constraints(nullable: true) }
            column(name: 'summary', type: 'VARCHAR(1000)', remarks: '��ǩ���')
                    { constraints(nullable: true) }
            column(name: 'tag_type', type: 'TINYINT(3) UNSIGNED', remarks: '��ǩ���ͣ��Ƽ������š����ԡ�֪ʶ����λ����˾��Ŀ�꣩', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'is_selection', type: 'TINYINT(3) UNSIGNED', remarks: '�Ƿ�ѡ', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'is_publish', type: 'TINYINT(3) UNSIGNED', remarks: '�Ƿ��ϼ�', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '״̬', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'click_count', type: 'INT(11)', remarks: '�����', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'sort', type: 'INT(11)', remarks: '�����ֶΣ�Խ��Խ��ǰ', defaultValue: 0)
                    { constraints(nullable: true) }
            column(name: 'tag_level', type: 'TINYINT(3)', remarks: '��ǩ�ȼ�', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'is_jump_external_url', type: 'TINYINT(3)', remarks: '�Ƿ���ת����', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'url', type: 'VARCHAR(1024)', remarks: '����')
                    { constraints(nullable: true) }
            column(name: 'icon', type: 'VARCHAR(255)', remarks: 'ͼ��')
                    { constraints(nullable: true) }
        }

    }

}
