# 部署mysql
- 编写配置文件`mysql.yaml`
```yaml
env:
  MYSQL_ROOT_PASSWORD: password
config:
  character_set_server: utf8mb4
  collation_server: utf8mb4_general_ci
  lower_case_table_names: 1
  max_allowed_packet: 32M
  max_connections: 1500

persistence:
  enabled: true
  storageClass: 
```

