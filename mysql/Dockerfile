FROM mysql:8.0

# 设置环境变量
ENV MYSQL_ROOT_PASSWORD=123456
ENV MYSQL_DATABASE=class_manage
ENV MYSQL_USER=class_manage
ENV MYSQL_PASSWORD=123456

# 设置时区
ENV TZ=Asia/Shanghai

# 复制初始化SQL脚本
COPY init/init.sql /docker-entrypoint-initdb.d/

# 设置字符集
CMD ["mysqld", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci"]
