# Imagem de execução (o JAR é gerado antes pelo "mvn clean package"; ver Jenkinsfile).
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/myapp.jar /app/myapp.jar
# Usuário sem privilégios
RUN addgroup -S app && adduser -S app -G app
USER app
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
