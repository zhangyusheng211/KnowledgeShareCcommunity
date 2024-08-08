#!/usr/bin/env bash


helm upgrade --install vue-mogu-admin ./vue-mogu-admin \
    -f vue-mogu-admin.yaml \
    --create-namespace \
    --namespace mogu-system

helm install \
./elasticsearch \
--name mogu-es \
--set image.repository=it00021hot/es \
--set image.tag=7.8.1 \
--set image.registry=registry.cn-shenzhen.aliyuncs.com \
--set master.replicas=3 \
--set master.heapSize=256m \
--set master.persistence.storageClass=mogu-nfs \
--set coordinating.heapSize=256m \
--set data.persistence.storageClass=mogu-nfs \
--set data.persistence.size=20Gi \
--set data.heapSize=1024m \
--set plugins=analysis-icu \
--set sysctlImage.enabled=false \
--set global.kibanaEnabled=true \
--set global.storageClass=mogu-nfs \
--namespace mogu-system

kubectl exec mysql-58d679df4c-w9k2t -n mogu-system -it -- mysqldump -uroot -proot nacos_config >  nacos_config.sql; sed -i '1d' nacos_config.sql

curl http://es.mogu.local.com/_cat/plugins

curl --header "Content-Type: application/json" 'http://es.mogu.local.com/_analyze?pretty=true' -XPOST -d '{"text":"蘑菇博客es中文测试","analyzer":"ik_smart"}'

kubectl get namespace rook-ceph -o json \
            | tr -d "\n" | sed "s/\"finalizers\": \[[^]]\+\]/\"finalizers\": []/" \
            | kubectl replace --raw /api/v1/namespaces/rook-ceph/finalize -f -

kubectl exec mogu-mysql-85cb88964-pppwh -n base-system -it -- mysqldump -uroot -ppassword nacos >  nacos.sql; sed -i '1d' nacos.sql

docker rmi registry.cn-shenzhen.aliyuncs.com/mogu-zh/vue-mogu-admin:latest
docker rmi registry.cn-shenzhen.aliyuncs.com/mogu-zh/vue-mogu-web:latest
helm uninstall -n mogu-system mogu-sms
helm uninstall -n mogu-system mogu-web
helm uninstall -n mogu-system mogu-admin
helm uninstall -n mogu-system mogu-picture
helm uninstall -n mogu-system vue-mogu-admin
helm uninstall -n mogu-system vue-mogu-web
helm uninstall -n mogu-system mogu-mysql
helm uninstall -n mogu-system mogu-redis
helm uninstall -n mogu-system minio
helm uninstall -n mogu-system nacos
helm uninstall -n mogu-system rabbitmq
helm uninstall -n mogu-system sentinel
helm uninstall -n mogu-system zipkin
