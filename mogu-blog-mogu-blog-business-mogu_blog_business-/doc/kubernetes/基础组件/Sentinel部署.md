# 添加helm chart仓库

```bash
helm repo add mogu-chart http://47.106.230.203:9090/
helm repo update
```
# 部署sentinel
### 编写配置文件`sentinel.yaml`
```bash
tee sentinel.yaml<<-EOF
env:
  open:
    # 本实例服务端口
    SERVER_PORT: 8080
    # 是否启用nacos持久化配置
    SENTINEL_DATASOURCE_NACOS_ENABLED: true
    # nacos地址
    SENTINEL_DATASOURCE_NACOS_SERVER_ADDR: 10.168.1.20:8848
    # nacos命名空间
    SENTINEL_DATASOURCE_NACOS_NAMESPACE: public
service:
  enabled: true
  type: ClusterIP
ingress:
  enabled: true
  host: sentinel.local.com
EOF
```
- 执行安装
```bash
helm upgrade --install sentinel mogu-chart/sentinel \
    -f sentinel.yaml \
    --create-namespace \
    --namespace base-system
```
