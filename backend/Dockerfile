FROM maven:3.9-eclipse-temurin-8 AS builder

WORKDIR /app

# 拷贝 Maven 配置并构建后端
COPY backend ./backend

# 使用单线程构建，避免在容器内获取 Maven 锁失败的问题
RUN mvn -f backend/pom.xml clean package -DskipTests

FROM eclipse-temurin:8-jre

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-Xms512m -Xmx1024m"

WORKDIR /app

COPY --from=builder /app/backend/ruoyi-admin/target/ruoyi-admin.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
