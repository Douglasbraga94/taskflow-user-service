# ————————————————————————
# Etapa 1: build da aplicação com Maven
# ————————————————————————
FROM maven:3.8.6-openjdk-17-slim AS build

WORKDIR /app

# Copia o pom.xml e os arquivos de build (opcional: cache de dependências)
COPY pom.xml ./
# Copia o diretório src
COPY src ./src

# Compila e empacota (skip tests para acelerar — você pode remover -DskipTests se quiser rodar testes)
RUN mvn clean package -DskipTests

# ————————————————————————
# Etapa 2: rodar a aplicação usando JRE leve
# ————————————————————————
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copia o JAR gerado da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponha a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
