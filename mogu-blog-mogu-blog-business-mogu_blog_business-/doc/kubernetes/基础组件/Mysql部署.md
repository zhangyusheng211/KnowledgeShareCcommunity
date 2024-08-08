# 添加helm chart仓库

```bash
helm repo add mogu-chart http://47.106.230.203:9090/
helm repo update
```
# 部署Mysql
### 编写配置文件`mysql.yaml`
```bash
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
EOF
```
- 执行安装
```bash
helm upgrade --install mogu-mysql mogu-chart/mysql \
    -f mysql.yaml \
    --create-namespace \
    --namespace base-system
```

- 参数：

  | 参数                         | 含义                     |
  | :--------------------------- | :----------------------- |
  | persistence.enabled          | 是否启用持久化存储       |
  | persistence.storageClass     | 存储卷的名称             |
  | persistence.subPath          | 设置将数据存储到的子目录 |
  | env.open.MYSQL_ROOT_PASSWORD | 设置数据库root用户密码   |
  | env.open.MYSQL_DATABASE      | 初始化创建的数据库名称   |
  | env.open.MYSQL_USER          | 初始化创建的用户名       |
  | env.open.MYSQL_PASSWORD      | 初始化创建的用户密码     |

### 其他操作
- 导出sql脚本
```bash
kubectl -n base-system exec -it $(kubectl -n base-system get pod -l "mysql/release=mogu-mysql" -o jsonpath='{.items[0].metadata.name}') -- mysqldump   -uroot -ppassword --databases nacos mogu_picture mogu_blog > mogu.sql; sed -i '2d' mogu.sql

```
- 导入sql脚本
```bash
kubectl -n base-system exec -it $(kubectl -n base-system get pod -l "mysql/release=mogu-mysql" -o jsonpath='{.items[0].metadata.name}') -- mysql -uroot -ppassword < mogu.sql

```
