# 介绍

初始化数据库工具，使用指定文件夹的groovy文件创建数据库表，使用execl文件(只能是.xlsx格式文件)初始化数据。

# 使用

### 初始化文件目录

- 初始化文件应放于资源目录script/db目录下
- groovy文件存放需要初始化的groovy脚本
- init-data文件存放需要初始化的excel文件
- service-mapping.xml初始化配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--服务映射-->
<services>
  <schema-merge>
      <!-- oracle merge是否默认安装到一个库下，若要分多个库，需设置 merge=false -->
      <oracle merge="false" target-schema="" />
      <mysql merge="false" target-schema="" />
      <sqlserver merge="false" target-schema="" />
      <postgresql merge="false" target-schema="" />
  </schema-merge>
   <!-- name和filename: 对应本地文件名，默认与安装目标库名一致;  schema: 安装目标库名 env:使用对应的数据源，非必填，多数据源初始化时需要用到 -->
    <service name="mogu_admin" filename="mogu_admin" schema="mogu_blog_business" description="蘑菇管理后台"/>
</services>
```
- init-mysql-database.sh包初始化脚本
```sh
#!/usr/bin/env bash
MAVEN_LOCAL_REPO=$(cd / && mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)
TOOL_GROUP_ID=io.choerodon
TOOL_ARTIFACT_ID=choerodon-tool-liquibase
TOOL_VERSION=0.17.1.RELEASE
TOOL_JAR_PATH=${MAVEN_LOCAL_REPO}/${TOOL_GROUP_ID/\./\/}/${TOOL_ARTIFACT_ID}/${TOOL_VERSION}/${TOOL_ARTIFACT_ID}-${TOOL_VERSION}.jar
mvn org.apache.maven.plugins:maven-dependency-plugin:get \
-Dartifact=${TOOL_GROUP_ID}:${TOOL_ARTIFACT_ID}:${TOOL_VERSION} \
-Dtransitive=false

#mvn clean package spring-boot:repackage

java -Dspring.datasource.url="jdbc:mysql://localhost:3306/?serverTimezone=CTT&useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true" \
 -Dspring.datasource.username=root \
 -Dspring.datasource.password=root \
 -Dspring.datasource.driver-class-name=com.mysql.jdbc.Driver \
 -Ddata.init=true \
 -Dlogging.level.root=info \
 -Dinstaller.jarPath=target/mogu-admin.jar \
 -jar ${TOOL_JAR_PATH}

```
 - -Dspring.datasource.url： 数据源url 不需要指定数据库名会根据配置的数据库名初始化到对应的数据库
 - -Dspring.datasource.username：数据库登陆用户
 - -Dspring.datasource.driver-class-name：数据库驱动，默认oracle
 - -Ddata.init：是否使用excel数据进行数据初始化
 - -Dlogging.level.root：日志级别
 - -Dinstaller.jarPath：groovy和excel所在的jar包，扫描.groovy和.xlsx文件
 - -Dinstaller.jarPath.init：是否递归扫描jar包中依赖的jar包中的.groovy和.xlsx文件
