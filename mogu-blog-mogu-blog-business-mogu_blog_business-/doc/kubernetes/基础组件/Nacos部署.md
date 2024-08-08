# 添加helm chart仓库

```bash
helm repo add mogu-chart http://47.106.230.203:9090/
helm repo update
```
# 部署nacos
### 导入数据库
- 导入DB中的数据库文件`nacos.sql`到mysql中
```bash
kubectl -n base-system exec -it $(kubectl -n base-system get pod -l "mysql/release=mogu-mysql" -o jsonpath='{.items[0].metadata.name}') -- mysql -uroot -ppassword nacos < nacos.sql

```


### 部署nacos

- 编写配置文件`nacos.yaml`

```bash
tee nacos.yaml<<-EOF
global:
  mode: standalone
mysql:
  rootPassword: password
  database: nacos
  user: nacos
  password: nacos
  port: 3306
env:
  dbHost: mogu-mysql  
service:
  type: ClusterIP
ingress:
  enabled: true
  hosts:
    - host: nacos.local.com  
resources:
  limits:
    cpu: 1000m
    memory: 2Gi
  requests:
    cpu: 1000m
    memory: 2Gi    
EOF
```

- 执行安装

```bash
helm upgrade --install nacos mogu-chart/nacos \
    -f nacos.yaml \
    --create-namespace \
    --namespace base-system
```



