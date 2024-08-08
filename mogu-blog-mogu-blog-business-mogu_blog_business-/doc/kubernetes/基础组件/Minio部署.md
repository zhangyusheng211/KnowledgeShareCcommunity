# 添加helm chart仓库

```bash
helm repo add mogu-chart http://47.106.230.203:9090/
helm repo update
```
# 部署minio
### 编写配置文件`minio.yaml`
```bash
tee minio.yaml<<-EOF
service:
  enabled: true
persistence:
  enabled: true
  storageClass: nfs-provisioner
ingress:
  enabled: true
  host: minio.local.com
  path: 
EOF
```
- 执行安装
```bash
helm upgrade --install minio mogu-chart/minio \
    -f minio.yaml \
    --create-namespace \
    --version 1.0.0	\
    --namespace base-system
```
