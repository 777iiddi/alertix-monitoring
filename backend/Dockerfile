# ============================================================================
# Alertix Monitoring - Backend Dockerfile
# Multi-stage build for optimized production image
# ============================================================================

# ============================================================================
# Stage 1: Build
# ============================================================================
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
COPY .mvn .mvn
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src src
RUN mvn clean package -DskipTests -B

# ============================================================================
# Stage 2: Runtime
# ============================================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -g 1001 -S alertix && \
    adduser -u 1001 -S alertix -G alertix

# Copy JAR from build stage
COPY --from=build /app/target/alertix-backend.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs && chown -R alertix:alertix /app

# Switch to non-root user
USER alertix

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
