ARG VERSION=18.0.1.1
FROM openjdk:${VERSION}-jdk as BUILD
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Dspring.profiles.active=aws", "-jar","app.jar"]

EXPOSE 8080