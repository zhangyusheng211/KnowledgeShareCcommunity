# 添加helm chart仓库

```bash
helm repo add mogu-chart http://47.106.230.203:9090/
helm repo update
```
# 部署rabbitmq
### 编写配置文件`rabbitmq.yaml`
```bash
tee rabbitmq.yaml<<-EOF
auth:
  username: mogumq
  password: mogumq
persistence:
  enabled: true
  storageClass: nfs-provisioner
  size: 1Gi
ingress:
  enabled: true
  hostname: rabbitmq.local.com
EOF
```
- 执行安装
```bash
helm upgrade --install rabbitmq mogu-chart/rabbitmq \
    -f rabbitmq.yaml \
    --create-namespace \
    --namespace base-system
```
