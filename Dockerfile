# Sử dụng môi trường Java 17
FROM eclipse-temurin:17-jdk

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Sao chép file JAR vào container
COPY target/*.jar app.jar

# Mở cổng 8080
EXPOSE 8080

# Lệnh chạy ứng dụng
CMD ["java", "-jar", "app.jar"]
