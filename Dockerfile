# Dockerfile

# jdk17 Image Start
FROM openjdk:17

ARG JAR_FILE=build/libs/information-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} information_Backend.jar
ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","information_Backend.jar"]