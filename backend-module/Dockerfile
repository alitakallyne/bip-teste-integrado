# Etapa 1: Build do projeto
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copia todo o projeto (o contexto será a raiz do repo)
COPY . .

# Faz o build (gera o .jar do backend)
RUN mvn -pl backend-module -am clean package -DskipTests

# Etapa 2: Runtime do Spring Boot
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o jar gerado
COPY --from=builder /app/backend-module/target/backend-module-*.jar app.jar

# Porta
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
