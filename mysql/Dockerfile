FROM mysql:8.0

ENV TZ=Asia/Shanghai
ENV MYSQL_ROOT_HOST=%

# 把初始化脚本直接COPY进镜像，避免macOS文件共享死锁问题
COPY init/01-schema.sql /docker-entrypoint-initdb.d/
COPY init/02-triggers.sql /docker-entrypoint-initdb.d/
COPY init/03-procedures.sql /docker-entrypoint-initdb.d/
COPY init/04-sample-data.sql /docker-entrypoint-initdb.d/
COPY init/05-performance-indexes.sql /docker-entrypoint-initdb.d/

EXPOSE 3306
