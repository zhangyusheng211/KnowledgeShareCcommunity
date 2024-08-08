FROM openjdk:8-jdk-alpine

ENV LANG C.UTF-8
ENV TZ Asia/Shanghai
# 设置华为云镜像源
RUN sed -i "s@http://dl-cdn.alpinelinux.org/@https://mirrors.huaweicloud.com/@g" /etc/apk/repositories
RUN apk --no-cache add tzdata && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" > /etc/timezone

MAINTAINER 15077731547@163.com

ENTRYPOINT []
CMD ["/bin/sh"]
