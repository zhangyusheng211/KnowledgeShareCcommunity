package script.db.groovy.mogu_admin


databaseChangeLog(logicalFilePath: 'script/db/groovy/mogu_admin/room.groovy') {
    changeSet(id: '2022-04-10-t-room', author: '15077731547@163.com') {
        createTable(tableName: "t_room", remarks: 'null') {
            column(name: 'uid', type: 'VARCHAR(32)', remarks: 'Ψһuid')
                    { constraints(primaryKey: true) }
            column(name: 'room_type', type: 'INT(5)', remarks: '���� Ⱥ��/����')
                    { constraints(nullable: true) }
            column(name: 'belong_user_id', type: 'VARCHAR(32)', remarks: '�Ự�����û�')
                    { constraints(nullable: true) }
            column(name: 'receive_id', type: 'VARCHAR(32)', remarks: '��Ϣ������')
                    { constraints(nullable: true) }
            column(name: 'avatar', type: 'VARCHAR(255)', remarks: '�Ựͷ��')
                    { constraints(nullable: true) }
            column(name: 'title', type: 'VARCHAR(255)', remarks: '�Ự����')
                    { constraints(nullable: true) }
            column(name: 'session_id', type: 'VARCHAR(32)', remarks: '�Ự��Ϣ��¼id')
                    { constraints(nullable: true) }
            column(name: 'status', type: 'TINYINT(3) UNSIGNED', remarks: '״̬', defaultValue: 1)
                    { constraints(nullable: false) }
            column(name: 'create_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
            column(name: 'update_time', type: 'TIMESTAMP', remarks: '����ʱ��')
                    { constraints(nullable: true) }
        }

    }

    changeSet(id: '2022-04-10-initData-t-t_room', author: 'İϪ') {
        sql(stripComments: true, splitStatements: true, endDelimiter: ';') {
            "insert  into `t_room`(`uid`,`room_type`,`belong_user_id`,`receive_id`,`avatar`,`title`,`session_id`,`status`,`create_time`,`update_time`) values ('10001',10002,'10000','10001','http://picture.moguit.cn//blog/admin/jpg/2020/12/21/1608539694443.jpg','Ģ����������Ⱥ','10001',1,NULL,NULL);\n"
        }
    }

}
