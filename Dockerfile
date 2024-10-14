# build 에서 사용할 이미지
FROM gradle:8.10.1-jdk17 AS build

WORKDIR /app

ARG FILE_DIRECTORY

COPY $FILE_DIRECTORY /app

RUN gradle clean bootJar

# 실제 컨테이너로 만들 이미지 베이스
FROM openjdk:17-jdk-slim

# build 단계로부터 파일을 가져올 수 있음!
# AS build로 선언해놨기에 --from=build!
COPY --from=build /app/build/libs/*SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "app.jar"]
