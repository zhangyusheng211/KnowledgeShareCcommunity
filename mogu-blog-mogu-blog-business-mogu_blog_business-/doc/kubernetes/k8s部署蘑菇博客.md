# k8s部署蘑菇博客

- 前提条件：已安装k8s、kubectl、helm3

# 基础组件部署

## [NFS动态卷](基础组件/NFS动态卷.md)
## [mysql部署](基础组件/Mysql部署.md)
## [redis部署](基础组件/Redis部署.md)
## [Minio部署](基础组件/Minio部署.md)
## [Rabbitmq部署](基础组件/Rabbitmq部署.md)
## [Nacos部署](基础组件/Nacos部署.md)
## [Sentinel部署](基础组件/Sentinel部署.md)
## [zipkin部署](基础组件/Zipkin部署.md)
# 后端服务部署
## 导入数据库
- 在kubernetes目录下执行以下命令
```bash
kubectl -n base-system exec -it $(kubectl -n base-system get pod -l "mysql/release=mogu-mysql" -o jsonpath='{.items[0].metadata.name}') -- mysql -uroot -ppassword  < DB/mogu_blog.sql
kubectl -n base-system exec -it $(kubectl -n base-system get pod -l "mysql/release=mogu-mysql" -o jsonpath='{.items[0].metadata.name}') -- mysql -uroot -ppassword  < DB/mogu_picture.sql
```

## 部署mogu-picture

### [mogu-picture部署](后端服务/mogu-picture.md)

## 部署mogu-sms

### [mogu-sms部署](后端服务/mogu-sms.md)

## 部署mogu-admin

### [mogu-admin部署](后端服务/mogu-admin.md)

## 部署mogu-web

### [mogu-web部署](后端服务/mogu-web.md)

# 前端服务部署

## 部署vue-mogu-admin

### [vue-mogu-admin部署](前端服务/vue-mogu-admin.md)

## 部署vue-mogu-web

### [vue-mogu-web部署](前端服务/vue-mogu-web.md)
