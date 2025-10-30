# Etapa 1: Build
FROM eclipse-temurin:21-alpine AS build
WORKDIR /app

# Copiar todo el proyecto
COPY . .

# Empaquetar sin correr tests
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-alpine
WORKDIR /app

# Copiar el jar generado desde el contenedor de build
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto por defecto de Spring WebFlux (usualmente 8080)
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
