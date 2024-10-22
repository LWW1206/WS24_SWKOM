FROM openjdk:17-jdk-alpine

RUN apk add --no-cache maven

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package

EXPOSE 8080

CMD ["java", "-jar", "target/DMS-0.0.1-SNAPSHOT.jar"]