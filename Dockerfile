# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom first for layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Run Stage ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
# TODO: In production, also drop capabilities and use read-only filesystem
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/target/app.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

# Configurable port
ENV PORT=8080
EXPOSE ${PORT}

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
