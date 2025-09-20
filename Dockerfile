# =============================
# Stage 1: Build
# =============================
FROM eclipse-temurin:21-jdk-alpine AS builder

# Configurar directorio de trabajo
WORKDIR /app

# Copiar archivos de Gradle necesarios para cache de dependencias
COPY gradle gradle
COPY gradlew .
COPY gradle.properties .
COPY settings.gradle .
COPY build.gradle .
COPY main.gradle .

# Hacer ejecutable el wrapper de Gradle
RUN chmod +x gradlew

# Descargar dependencias para cachear
RUN ./gradlew dependencies --no-daemon || true

# Copiar código fuente completo
COPY applications applications
COPY domain domain
COPY infrastructure infrastructure

# Construir el módulo app-service (sin tests para acelerar)
RUN ./gradlew clean build -x validateStructure -x test --no-daemon

# =============================
# Stage 2: Runtime
# =============================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar jar construido desde builder
COPY --from=builder /app/applications/app-service/build/libs/CrediYaLoanRequest.jar ./CrediYaLoanRequest.jar

# Exponer puerto de la aplicación
EXPOSE 8080

# Comando por defecto para ejecutar
ENTRYPOINT ["java","-jar","CrediYaLoanRequest.jar"]
