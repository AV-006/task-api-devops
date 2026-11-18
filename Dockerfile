# Build stage: Maven and the full JDK are used only while compiling the app.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

# Runtime stage: only the JRE and executable JAR are shipped.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /workspace/target/task-api.jar /app/task-api.jar
USER app
ENV APP_PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/task-api.jar"]
