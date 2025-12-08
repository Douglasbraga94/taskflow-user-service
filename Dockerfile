# Etapa 1: Build da aplicação com Maven e OpenJDK 17
FROM openjdk:17-slim AS build

# Instalar Maven
RUN apt-get update && apt-get install -y maven

WORKDIR /app

# Copiar os arquivos do projeto
COPY . .

# Construir o projeto com Maven
RUN mvn clean package -DskipTests

# Etapa 2: Rodar a aplicação com uma imagem leve
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copiar o JAR gerado pela etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expor a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
