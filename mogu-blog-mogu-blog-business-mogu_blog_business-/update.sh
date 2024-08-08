#!/usr/bin/env bash
echo '=====开始安装蘑菇博客环境====='
cd /www/wwwroot/data/maven/build/drone-study/mogu_gateway
docker build  -t registry.cn-shenzhen.aliyuncs.com/mogublog_business/mogu_gateway .

cd /www/wwwroot/data/maven/build/drone-study/mogu_picture
docker build  -t registry.cn-shenzhen.aliyuncs.com/mogublog_business/mogu_picture .

cd /www/wwwroot/data/maven/build/drone-study/mogu_sms
docker build  -t registry.cn-shenzhen.aliyuncs.com/mogublog_business/mogu_sms .

cd /www/wwwroot/data/maven/build/drone-study/mogu_admin
docker build  -t registry.cn-shenzhen.aliyuncs.com/mogublog_business/mogu_admin .

cd /www/wwwroot/data/maven/build/drone-study/mogu_spider
docker build  -t registry.cn-shenzhen.aliyuncs.com/mogublog_business/mogu_spider .

cd /www/wwwroot/data/maven/build/drone-study/mogu_monitor
docker build -t registry.cn-shenzhen.aliyuncs.com/mogublog_business/mogu_monitor .

cd /www/wwwroot/data/maven/build/drone-study/mogu_search
docker build  -t registry.cn-shenzhen.aliyuncs.com/mogublog_business/mogu_search .

cd /www/wwwroot/data/maven/build/drone-study/mogu_web
docker build -t registry.cn-shenzhen.aliyuncs.com/mogublog_business/mogu_web .

cd /www/wwwroot/data/maven/build/drone-study/vue_mogu_web
docker build -t registry.cn-shenzhen.aliyuncs.com/mogublog_business/vue_mogu_web .

cd /www/wwwroot/data/maven/build/drone-study/vue_mogu_admin
docker build -t registry.cn-shenzhen.aliyuncs.com/mogublog_business/vue_mogu_admin .