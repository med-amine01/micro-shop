FROM openjdk:17-alpine
WORKDIR application
ENV TZ="Africa/Tunis"
ADD target/*.jar app.jar
ENTRYPOINT ["java","-jar","/application/app.jar"]