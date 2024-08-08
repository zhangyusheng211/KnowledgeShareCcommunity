# 添加helm chart仓库

```bash
helm repo add mogu-chart http://47.106.230.203:9090/
helm repo update
```
# 部署zipkin
### 编写配置文件`zipkin.yaml`
```bash
tee zipkin.yaml<<-EOF
env:
  open:
    # 本实例服务端口
    SERVER_PORT: 9411
service:
  enabled: true
  type: ClusterIP
ingress:
  enabled: true
  host: zipkin.local.com
EOF
```
- 执行安装
```bash
helm upgrade --install zipkin mogu-chart/zipkin \
    -f zipkin.yaml \
    --create-namespace \
    --namespace base-system
```
