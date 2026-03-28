# 构建阶段
FROM maven:3.9.6-amazoncorretto-17 AS builder

WORKDIR /app

# 复制pom.xml并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制源代码并编译
COPY src ./src
RUN mvn package -DskipTests

# 运行阶段
FROM amazoncorretto:17-alpine

WORKDIR /app

# 复制编译好的jar包
COPY --from=builder /app/target/class-manage-backend-1.0.0.jar app.jar

# 设置时区
ENV TZ=Asia/Shanghai

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
