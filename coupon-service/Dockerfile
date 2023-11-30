FROM openjdk:17-alpine
RUN apk --no-cache add curl
WORKDIR application
ENV TZ="Africa/Tunis"
ADD target/*.jar app.jar
VOLUME /application-data
ENTRYPOINT ["java", "-jar", "/application/app.jar"]
