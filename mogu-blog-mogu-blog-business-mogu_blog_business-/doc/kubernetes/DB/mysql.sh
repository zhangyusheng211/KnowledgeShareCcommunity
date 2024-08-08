#!/bin/bash

tee mysql.yaml<<-EOF
config:
  character_set_server: utf8mb4
  collation_server: utf8mb4_general_ci
  lower_case_table_names: 1
  max_allowed_packet: 32M
  max_connections: 1500
env:
  # must
  MYSQL_ROOT_PASSWORD: password
  TZ: "Asia/Shanghai"
  # 创建nacos数据库
  MYSQL_DATABASE: nacos
  MYSQL_USER: nacos
  MYSQL_PASSWORD: nacos
service:
  enabled: true
persistence:
  enabled: true
  storageClass: nfs-provisioner
  accessMode: ReadWriteOnce
  size: 1Gi
init: |+
$(while read -r line || [[ -n "$line" ]]; do   printf '    %s\n' "$line"; done < init.sql)
EOF

helm uninstall mogu-mysql -n test-system
helm upgrade --install mogu-mysql ../../../helm/mysql/     -f mysql.yaml     --create-namespace     --namespace test-system
