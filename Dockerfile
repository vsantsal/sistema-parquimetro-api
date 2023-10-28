# Etapa 1: Construção da aplicação com Maven e Java 17
FROM maven:3.8.4-openjdk-17 AS builder

WORKDIR /app

# Cópia do código-fonte da aplicação
COPY src ./src


# Cópia do arquivo pom.xml (deps da aplicação)
COPY pom.xml .

# Baixar deps
RUN mvn dependency:go-offline

# Compilar e testar a aplicação
RUN mvn clean install

# Etapa 2: Execução do JAR Gerado
FROM openjdk:17-slim

WORKDIR /app

# Cópia do JAR gerado da etapa 1
COPY --from=builder /app/target/sistema-parquimetro-1.0.0-SNAPSHOT.jar .

# Execução do JAR
CMD ["java", "-jar", "sistema-parquimetro-1.0.0-SNAPSHOT.jar"]