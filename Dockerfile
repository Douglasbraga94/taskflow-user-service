# --------------------------------------------------------------------------
# ESTÁGIO 1: BUILD (Compilação do Projeto)
# --------------------------------------------------------------------------
# Utiliza uma imagem com JDK e Maven para compilar o projeto.
FROM maven:3.9.6-eclipse-temurin-17-focal AS build

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia os arquivos de configuração do Maven (pom.xml) e baixa as dependências
# Isso otimiza o cache do Docker, pois a camada de dependências só é recriada se o pom.xml mudar.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia todo o código-fonte
COPY src ./src

# Executa o build da aplicação, gerando o JAR final em 'target/'
# O -DskipTests é usado para acelerar o build em produção (opcional)
RUN mvn package -DskipTests

# --------------------------------------------------------------------------
# ESTÁGIO 2: PRODUÇÃO (Execução do JAR)
# --------------------------------------------------------------------------
# Usa uma imagem JRE minimalista e segura para o ambiente de execução.
FROM eclipse-temurin:17-jre-focal AS production

# Define o diretório de trabalho
WORKDIR /app

# A maioria dos serviços web no Render deve estar configurada para escutar na porta 10000.
# O Spring Boot usa 8080 por padrão.
# EXPOSE 8080

# Copia o arquivo JAR compilado do estágio de build para o diretório /app da imagem final.
# O nome do JAR é capturado com um curinga e renomeado para app.jar para simplificar.
COPY --from=build /app/target/*.jar /app/app.jar

# Ponto de entrada (Entrypoint) do contêiner.
# Executa o JAR. A porta de execução deve ser configurada via Variável de Ambiente.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
