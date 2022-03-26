# Maven seems to build image for x86 platform.
# see https://stackoverflow.com/questions/69526553

FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
