FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y tesseract-ocr tesseract-ocr-eng ghostscript

WORKDIR /app

COPY ./target/paperless-services-0.0.1-SNAPSHOT.jar app.jar


EXPOSE 8082

CMD ["java", "-jar", "app.jar"]