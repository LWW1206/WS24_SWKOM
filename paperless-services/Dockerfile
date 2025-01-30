FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y tesseract-ocr tesseract-ocr-eng ghostscript
WORKDIR /app
COPY target/paperless-0.0.1-SNAPSHOT.jar paperless-worker.jar
EXPOSE 8082