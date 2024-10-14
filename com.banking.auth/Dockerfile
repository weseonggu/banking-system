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
COPY com.banking.auth ./com.banking.auth

# 환경 변수 정의
ARG SPRING_PROFILES_ACTIVE=dev

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

# 빌드 수행 (테스트 포함)
RUN ./gradlew clean build :com.banking.auth:build

# 실행 이미지 생성
FROM openjdk:17-jdk-slim
WORKDIR /app

# JAR 파일 복사
COPY --from=build /app/com.banking.auth/build/libs/*SNAPSHOT.jar ./app.jar

# Set environment variables if needed
ENV SPRING_PROFILES_ACTIVE=dev

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
