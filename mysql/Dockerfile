FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD=root123
ENV MYSQL_DATABASE=class_manage
ENV MYSQL_USER=classmanage
ENV MYSQL_PASSWORD=classmanage123

COPY ./init/ /docker-entrypoint-initdb.d/

EXPOSE 3306

CMD ["mysqld"]
