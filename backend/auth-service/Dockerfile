FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S velixa && adduser -S velixa -G velixa
COPY --from=builder /app/target/*.jar app.jar
RUN chown velixa:velixa app.jar
USER velixa
EXPOSE 8081
ENTRYPOINT ["java", \
  "-jar", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "app.jar"]
