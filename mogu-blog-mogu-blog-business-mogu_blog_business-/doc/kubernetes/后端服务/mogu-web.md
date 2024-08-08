# 添加helm chart仓库

```bash
helm repo add mogu-chart http://47.106.230.203:9090/
helm repo update
```

# 部署mogu-web

### 编写配置文件`mogu-web.yaml`

```bash
tee mogu-web.yaml<<-EOF
env:
  open:
    # 本实例运行的配置环境
    SPRING_PROFILES_ACTIVE: test
    # 本实例服务端口
    SERVER_PORT: 8603
    # 注册中心地址
    SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR: nacos-cs.base-system:8848
    # 注册中心命名空间
    SPRING_CLOUD_NACOS_DISCOVERY_NAMESPACE: test
    # 配置中心地址
    SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR: nacos-cs.base-system:8848
    # 配置文件格式
    SPRING_CLOUD_NACOS_CONFIG_FILE_EXTENSION: yaml
    #指定分组
    SPRING_CLOUD_NACOS_CONFIG_GROUP: test
    # 指定命名空间
    SPRING_CLOUD_NACOS_CONFIG_NAMESPACE: test
    # sentinel流控地址
    SPRING_CLOUD_SENTINEL_TRANSPORT_DASHBOARD: sentinel.base-system:8080
    # sentinel交互端口
    SPRING_CLOUD_SENTINEL_TRANSPORT_PORT: 8719
ingress:
  enabled: true
  host: web.mogu.local.com
EOF
```

- 执行安装

```bash
helm upgrade --install mogu-web mogu-chart/mogu-web \
    -f mogu-web.yaml \
    --create-namespace \
    --namespace mogu-system
```

