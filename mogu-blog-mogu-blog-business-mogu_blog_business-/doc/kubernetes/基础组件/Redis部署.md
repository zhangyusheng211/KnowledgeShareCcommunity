# 添加helm chart仓库

```bash
helm repo add mogu-chart http://47.106.230.203:9090/
helm repo update
```
# 部署redis
### 编写配置文件`redis.yaml`
```bash
tee redis.yaml<<-EOF
service:
  enabled: true
  type: ClusterIP
persistence:
  enabled: true
  storageClass: nfs-provisioner
  accessMode: ReadWriteOnce
EOF
```
- 执行安装
```bash
helm upgrade --install mogu-redis mogu-chart/redis \
    -f redis.yaml \
    --create-namespace \
    --version 0.2.6	\
    --namespace base-system
```
