## Dockerfile for com.banking.auth
#FROM gradle:7.6-jdk17 AS build
#
## 작업 디렉토리 설정
#WORKDIR /app
#
## 루트 프로젝트의 build.gradle 파일과 settings.gradle 파일 복사
#COPY settings.gradle ./
#COPY build.gradle ./
## Gradle Wrapper 파일을 복사하고 종속성 다운로드
#COPY gradlew ./
#COPY gradle ./gradle
#
## 모듈 코드 복사
#COPY . .
#
## 환경 변수 정의 (Docker Compose에서 args로 설정될 수 있도록)
#ARG SPRING_PROFILES_ACTIVE
#ARG MODULE
#
## 환경 변수 설정
#ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
#
## 빌드 수행 (테스트 포함)
#RUN ./gradlew clean :com.banking.${MODULE}:build
#
## 실행 이미지 생성
#FROM openjdk:17-jdk-slim
#WORKDIR /app
#
## JAR 파일 복사
#COPY --from=build /app/com.banking.${MODULE}/build/libs/*SNAPSHOT.jar ./${MODULE}app.jar
#
## Set environment variables if needed
#ENV SPRING_PROFILES_ACTIVE=native
#
## Run the application
#ENTRYPOINT ["java", "-jar", "${MODULE}app.jar"]







## 1. Gradle 이미지 사용
#FROM gradle:8.10.1-jdk17 AS build
#
## 작업 디렉터리 설정
#WORKDIR /app
#
## 전체 프로젝트 복사
#COPY . /app
#
## 2. 먼저 공통 모듈 빌드
## RUN gradle :commonClass:clean :commonClass:build --no-daemon
#RUN gradle :com.banking.commonBean:clean :com.banking.commonBean:build --no-daemon
#
## 3. 이후 나머지 모듈 빌드 (의존성 해결 후)
#RUN gradle clean bootJar --no-daemon
#
## 실제 컨테이너로 만들 이미지 베이스
#FROM openjdk:17-jdk-slim
#
## build 단계로부터 파일을 가져올 수 있음!
## AS build로 선언해놨기에 --from=build!
#COPY --from=build /app/build/libs/*SNAPSHOT.jar /app.jar
#
#CMD ["java", "-jar", "app.jar"]


# Dockerfile for com.banking.auth
FROM gradle:7.6-jdk17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# 루트 프로젝트의 build.gradle 파일과 settings.gradle 파일 복사
COPY settings.gradle ./
COPY build.gradle ./
# Gradle Wrapper 파일을 복사하고 종속성 다운로드
COPY gradlew ./
COPY gradle ./gradle

# 모듈 코드 복사
COPY . .

# 환경 변수 정의 (Docker Compose에서 args로 설정될 수 있도록)
ARG SPRING_PROFILES_ACTIVE
ARG MODULE

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

# 빌드 수행 - 로그를 파일에 저장
RUN ./gradlew clean :com.banking.${MODULE}:bootJar


# 실행 이미지 생성
FROM openjdk:17-jdk-slim
WORKDIR /app
ARG SPRING_PROFILES_ACTIVE
ARG MODULE
# JAR 파일 복사
COPY --from=build /app/com.banking.${MODULE}/build/libs/*SNAPSHOT.jar ./app.jar

# Set environment variables if needed
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]



