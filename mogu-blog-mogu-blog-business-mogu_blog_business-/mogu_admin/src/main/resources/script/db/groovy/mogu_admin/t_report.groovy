package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/report.groovy') {
    changeSet(id: '2022-04-10-t-report', author: '15077731547@163.com') {
        createTable(tableName: "t_report", remarks: '���ݾٱ���Ϣ') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: '����')
                    { constraints(primaryKey: true) }
            column(name: 'report_type', type: 'VARCHAR(50)', remarks: '���ࣨ����/�ʴ�/���ۣ�')
                    { constraints(nullable: true) }
            column(name: 'report_user_uid', type: 'VARCHAR(50)', remarks: '���ٱ���uid')
                    { constraints(nullable: true) }
            column(name: 'report_content_uid', type: 'VARCHAR(50)', remarks: 'uid')
                    { constraints(nullable: true) }
            column(name: 'user_uid', type: 'VARCHAR(50)', remarks: '�ٱ���uid')
                    { constraints(nullable: true) }
            column(name: 'progress', type: 'TINYINT(3) UNSIGNED', remarks: '��չ״̬: 0 δ����   1: �Ѳ鿴  2���Ѵ���', defaultValue: 0)
                    { constraints(nullable: false) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '״̬  ��0 ɾ��  1������', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'violation_type', type: 'VARCHAR(255)', remarks: 'Υ������')
                    { constraints(nullable: true) }
            column(name: 'content', type: 'VARCHAR(255)', remarks: '�ٱ�����')
                    { constraints(nullable: true) }
            column(name: 'report_content', type: 'TEXT', remarks: '�ٱ����⡢����')
                    { constraints(nullable: true) }
        }

    }

}
