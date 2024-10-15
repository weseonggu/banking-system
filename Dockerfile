# # Dockerfile for com.banking.auth
# FROM gradle:7.6-jdk17 AS build

# # 작업 디렉토리 설정
# WORKDIR /app

# # 루트 프로젝트의 build.gradle 파일과 settings.gradle 파일 복사
# COPY settings.gradle ./
# COPY build.gradle ./
# # Gradle Wrapper 파일을 복사하고 종속성 다운로드
# COPY gradlew ./
# RUN chmod +x ./gradlew  # 실행 권한 부여
# COPY gradle ./gradle

# # COPY gradlew ./
# # COPY gradle ./gradle

# # 모듈 코드 복사
# COPY . .

# # 환경 변수 정의 (Docker Compose에서 args로 설정될 수 있도록)
# ARG TEST_SPRING_PROFILES_ACTIVE
# ARG MODULE

# # 환경 변수 설정
# ENV SPRING_PROFILES_ACTIVE=${TEST_SPRING_PROFILES_ACTIVE}

# # 빌드 수행 - 로그를 파일에 저장
# RUN ./gradlew clean :com.banking.${MODULE}:bootJar -x test


# # 실행 이미지 생성
# FROM openjdk:17-jdk-slim
# WORKDIR /app
# ARG SPRING_PROFILES_ACTIVE_PROD
# ARG MODULE
# # JAR 파일 복사
# COPY --from=build /app/com.banking.${MODULE}/build/libs/*SNAPSHOT.jar ./app.jar

# # SeEt environment variables if needed

# # Run the application
# ENTRYPOINT ["java", "-jar", "app.jar"]
# Dockerfile for com.banking.auth

FROM gradle:7.6-jdk17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 복사 및 실행 권한 부여
COPY gradlew ./  
RUN chmod +x ./gradlew  # 권한 부여

# Gradle 설정 파일 및 의존성 복사
COPY settings.gradle ./  
COPY build.gradle ./  
COPY gradle ./gradle

# 애플리케이션 코드 복사
COPY . .

# 환경 변수 정의 (Docker Compose로부터 전달될 수 있음)
ARG TEST_SPRING_PROFILES_ACTIVE
ARG MODULE

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=${TEST_SPRING_PROFILES_ACTIVE}

# Gradle 빌드 실행 - 테스트 제외
RUN ./gradlew clean :com.banking.${MODULE}:bootJar -x test

# 실행 이미지를 생성합니다.
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY --from=build /app/build/libs/*SNAPSHOT.jar ./app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
