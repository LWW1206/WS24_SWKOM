FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/DMS-0.0.1-SNAPSHOT.jar paperless-rest.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "paperless-rest.jar"]