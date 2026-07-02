#!/bin/bash
echo "Waiting for MySQL to be ready..."
until mysqladmin ping -h mysql -uroot -proot123456 --silent; do
    echo "MySQL is unavailable - sleeping"
    sleep 2
done
echo "MySQL is up - continuing..."
java -jar /app.jar
