FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache --upgrade gnutls libcrypto3 libssl3 libpng zlib
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
