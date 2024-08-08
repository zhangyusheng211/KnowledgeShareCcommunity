FROM registry.cn-shenzhen.aliyuncs.com/mogu-zh/jdk:8-mogu-alpine
ENV LANG C.UTF-8
ENV TZ Asia/Shanghai
VOLUME /tmp
ADD app.jar app.jar
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-jar","/app.jar"]
