FROM maven:3.6-jdk-11-openj9 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /build/src/
RUN mvn package

FROM adoptopenjdk/openjdk11-openj9:latest
EXPOSE 8080
CMD exec java $JAVA_OPTS -jar /app/my-app.jar
WORKDIR /app
COPY --from=builder /build/target/*.jar /app/my-app.jar
