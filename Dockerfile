# -------- Build Stage --------
FROM gradle:8.7.0-jdk21 AS build

# Set working directory
WORKDIR /app

# Copy only gradle files first (to leverage Docker cache)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Download dependencies
RUN gradle build -x test --no-daemon || true

# Copy full source and build
COPY . .
RUN gradle clean bootJar -x test --no-daemon

# -------- Runtime Stage --------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
