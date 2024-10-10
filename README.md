<br>

# MSA 기반의 온라인 은행 뱅킹 시스템 플랫폼

![image](https://media.istockphoto.com/id/640267784/ko/%EC%82%AC%EC%A7%84/%EC%9D%80%ED%96%89-%EB%B9%8C%EB%94%A9.jpg?s=2048x2048&w=is&k=20&c=3xvVGMcsLIwldGMgKBYZ2_5InzgXc3t6SB1V1tayWJw=)


## 📝 Technologies & Tools (BE) 📝

<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/> <img src="https://img.shields.io/badge/SpringSecurity-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white"/> ![Azure](https://img.shields.io/badge/springcloudgateway-%23FF00DD.svg?style=for-the-badge&logo=microsoftazure&logoColor=white) <img src="https://img.shields.io/badge/JSONWebToken-000000?style=for-the-badge&logo=JSONWebTokens&logoColor=white"/> 

<img src="https://img.shields.io/badge/postgres-%234169E1.svg?style=for-the-badge&logo=postgresql&logoColor=white"> <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white"/> <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/> <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"/>

<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/> ![Azure](https://img.shields.io/badge/zipkin-%230072C6.svg?style=for-the-badge&logo=microsoftazure&logoColor=white) <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"/> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"/> 

<img src="https://img.shields.io/badge/IntelliJIDEA-000000?style=for-the-badge&logo=IntelliJIDEA&logoColor=white"/>  <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white"/> <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white"/> <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"/>




## 👨‍👩‍👧‍👦 Our Team

|                    김원기                     |                      김혜린                       |                 위성구                 |                 이범수                 |
|:------------------------------------------:|:----------------------------------------------:|:-----------------------------------:|:-----------------------------------:|
| [@TrendFollow](https://github.com/TrendFollow) | [@uzuberceuse](https://github.com/uzuberceuse) | [@weseonggu](https://github.com/weseonggu) | [@beomsu1](https://github.com/beomsu1) |
|                     BE                     |                       BE                       |                 BE                  |                 BE                  |
<br>

### 개발 기간
`2024.09.24 ~ 2024.10.25`

## 프로젝트 간단 소개

### 📌 MSA 기반의 온라인 은행 뱅킹 시스템 플랫폼


## 프로젝트 기능
### 🙋🏻‍ 회원 (CRUD)
> * 회원 : 가입(등록), 로그인, 개별조회, 권한수정, 탈퇴, 전체조회(검색)
> * 배송 담당자: 등록, 개별조회, 타입수정, 삭제 전체조회(검색)
 

### 🏭 허브 (CRUD)
> * 애플리케이션 초기 시점 전국 17 개 허브 생성
> * 허브 정보 수정 및 삭제 기능 지원
> * 허브 관리에 대한 권한별 접근 제어 구현

### 🏢 업체 (CRUD)
> * 업체 등록, 수정 시 업체 주인의 권한인 사람 등록, 수정 가능 (마스터, 허브 관리자 대리 등록, 수정 가능)
> * 업체 삭제 시 등록된 상품 같이 삭제
> * 상품 등록, 수정 시 업체 주인의 권한인 사람 등록, 수정 가능 (마스터, 허브 관리자 대리 등록, 수정 가능)

### 🤖 AI (CRUD)
> * AI 답변 받은 내용 조회 구현
> * AI 답변 내용 삭제 (마스터만 가능)

### 🚛 주문 (CRUD)
> * 허브 경로 생성 - 애플리케이션 초기 시점 허브 리스트 조회 후 허브 간 이동 경로 생성 및 저장 
> * 주문 : 주문 CRUD - 주문 생성 시 주문 상품과 배송, 배송 상세 정보를 함께 생성하며, 주문 상품과 배송 상세 정보는 주문 정보와 함께 조회됩니다.
> * 주문상품 : 주문상품 CRUD - 주문 상품은 주문 생성 시 함께 생성되며, 상품 서버에 재고 확인 후 주문 상품 생성합니다.
> * 배송 : 배송 CRUD - 배송은 주문 생성 시 함께 생성되며, 배송 경로 및 배송 담당자 정보와 함께 조회됩니다.
> * 배송 경로 : 경로 조회 - 허브 간 이동 경로에 따라 최적의 배송경로 순서를 조회합니다.

### 🔎 검색 기능 
> * 각 도메인 별 검색은 QueryDSL을 활용하여 동적 쿼리작성이 가능하도록 구현하였습니다.
> * 검색 조건 및 정렬(생성일 순, 수정일 순) 기능을 제공하며 페이징 처리를 통해 반환되는 데이터 양을 제한하였습니다.



<br><br>



## 적용 기술

### ◻ QueryDSL

> * 정렬, 검색어 등에 따른 동적 쿼리 작성을 위하여 QueryDSL 도입하여 활용했습니다.


### ◻ [Swagger](http://localhost:19092/swagger)

> * 프론트엔드와 정확하고 원활한 소통을 위하여 스웨거를 도입하여 적용하였습니다.


### ◻ Redis

> * 연속된 요청으로 인한 데이터베이스 병목 현상을 해소하기 위해 다음과 같은 방법을 적용했습니다
> 1. DB 부하 감소 및 응답 속도 향상: Redis 캐시를 활용하여 데이터베이스의 부하를 줄이고 응답 속도를 개선했습니다. 주로 세션 관리 및 빠른 데이터 접근을 위한 캐시로 활용했습니다.
> 2. 데이터 불일치 문제 해결: Look Aside와 Write-Through 방식을 적용하여 데이터 불일치 문제를 효과적으로 해결했습니다.

### ◻ Spring Cloud Gateway
> * MSA 기반 서비스의 확장성과 효율성을 높이기 위해 도입하였습니다.
> * Client의 요청을 적절한 서비스로 라우팅하고, 필요에 따라 부하를 분산시켜 서비스의 안정성을 높였습니다.
> * 인증된 토큰을 검증하여 사용자의 정보를 헤더에 담아서 요청받은 서비스로 라우팅 하도록 설정했습니다.

### ◻ Resilience4j
> * 허브 간 이동정보 생성 시 외부 API 호출로 인한 장애를 방지하기 위해 도입하였습니다.
> * 3회 이상의 재시도로 인한 장애를 방지하고, 그 이상 장애 발생 시 보상 로직을 통해 경로에 데이터에 대한 정합성 유지 및 서비스 안정성을 높였습니다.

### ◻ zipkin
> * Zipkin 을 활용하여 MSA에서 분산 추적 기능을 구현하였으며, 각 서비스 간의 호출 흐름을 시각화하여 병목 현상 등을 빠르게 확인 할 수 있도록 하였습니다.

### ◻ OPEN API
> * Gemini AI: 매일 오전 8시 허브별 주문 정보를 분석하고, 이를 요약하여 공통 배송 담당자에게 슬랙 메시지로 자동 전송하는 기능을 구현했습니다. AI 기반의 데이터 처리 및 요약 기능을 통해 업무 효율성을 높였습니다.
> * 날씨 API: 매일 오전 6시 허브 별 날씨 정보 데이터를 번역하여 각 업체 배송 담당자의 슬랙으로 담당 허브 날씨 데이터를 전송하는 기능을 구현했습니다.
> * NAVER Direction 5: 허브 간 실제 이동 거리, 시간을 계산하여 경로 최적화 알고리즘을 적용하여 배송 효율을 높였습니다. 

### ㅁ Spring Security
> * 서비스의 보안성을 높이기 위해 도입하였습니다.
> * Auth 서비스 - 사용자의 정보를 입력받아 사용자 정보를 인증하여 유효한 토큰을 발급했습니다.
> * Other(요청 받은) 서비스 - Gateway를 통해 검증된 사용자의 정보를 토대로 사용자 정보를 검증하고 인가처리 했습니다.


<br><br>
## ⚙ Development Environment

`Java 17` `SpringBoot 3.3.3` `QueryDSL 5.0.0`


<br><br>

## 🚨 Trouble Shooting

#### Feign Client로 PATCH method 사용 불가 문제 [WIKI보기](https://github.com/sparta-nuTTTy/nuttty-delivery/wiki/%5BTrouble-Shooting%5D-Feign-Client%EB%A1%9C-PATCH-method-%EC%82%AC%EC%9A%A9-%EB%B6%88%EA%B0%80-%EB%AC%B8%EC%A0%9C#%EB%AC%B8%EC%A0%9C-%EC%A0%95%EC%9D%98)

#### 소프트 딜리트로 인해 유티크 제약 설정 문제 [WIKI보기](https://github.com/sparta-nuTTTy/nuttty-delivery/wiki/%5BTrouble-Shooting%5D-%EC%86%8C%ED%94%84%ED%8A%B8-%EB%94%9C%EB%A6%AC%ED%8A%B8%EB%A1%9C-%EC%9D%B8%ED%95%B4-%EC%9C%A0%EB%8B%88%ED%81%AC-%EC%A0%9C%EC%95%BD-%EC%84%A4%EC%A0%95-%EB%AC%B8%EC%A0%9C)

#### 기상청 API 호출 시 URL 인코딩 문제 [WIKI보기](https://github.com/sparta-nuTTTy/nuttty-delivery/wiki/%5BTrouble-Shooting%5D-%EA%B8%B0%EC%83%81%EC%B2%AD-API-%ED%98%B8%EC%B6%9C-%EC%8B%9C-URL-%EC%9D%B4%EC%A4%91-%EC%9D%B8%EC%BD%94%EB%94%A9-%EB%AC%B8%EC%A0%9C)

#### Jwt기반의 인증인가 보안성(Security 추가 적용) [WIKI보기](https://github.com/sparta-nuTTTy/nuttty-delivery/wiki/%5BTrouble-Shooting%5D-Jwt%EA%B8%B0%EB%B0%98-%EC%9D%B8%EC%A6%9D%EC%9D%B8%EA%B0%80-%EB%B0%A9%EC%8B%9D-%F0%9F%91%89-Jwt-&-Security-%EA%B8%B0%EB%B0%98%EC%9D%98-%EC%9D%B8%EC%A6%9D%EC%9D%B8%EA%B0%80-%EB%B0%A9%EC%8B%9D)

#### @PostConstruct에서 @Transactional 처리 시 문제 [WIKI보기](https://github.com/sparta-nuTTTy/nuttty-delivery/wiki/%5BTrouble-Shooting%5D-@PostConstruct%EC%97%90%EC%84%9C-@Transactional-%EC%B2%98%EB%A6%AC-%EC%8B%9C-%EB%AC%B8%EC%A0%9C%EC%A0%90)

#### Jackson라이브러리 snake_case, camelCase 변환 문제 [WIKI보기](https://github.com/sparta-nuTTTy/nuttty-delivery/wiki/%5BTrouble-Shooting%5D-Jackson%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC-snake_case,-camelCase-%EB%B3%80%ED%99%98-%EB%AC%B8%EC%A0%9C)

#### Redis 페이지 검색 조건 키 설정 문제 [WIKI보기](https://github.com/sparta-nuTTTy/nuttty-delivery/wiki/%5BTrouble-Shooting%5D-Redis-%ED%8E%98%EC%9D%B4%EC%A7%80-%EA%B2%80%EC%83%89-%EC%A1%B0%EA%B1%B4-%ED%82%A4-%EC%84%A4%EC%A0%95-%EB%AC%B8%EC%A0%9C)

#### Redis 직렬화 문제 [WIKI보기]

#### key userInfoCache:role:loggedUser:targetUser [WIKI보기]

<br><br>

## :raising_hand::thought_balloon: Concern

#### 메시징 시스템을 사용하여 각 서비스 간 비동기적으로 메시지를 전송하고, 이벤트 기반 통신을 구현

#### 모니터링을 통해 병목지점이 확인 되는 곳을 찾아 개선 해보기


<br><br>

## [📕  테이블 명세서](https://teamsparta.notion.site/db239348e87b4e6a9ae1ce404dc5ae2e)

<br>

## [📗  API 명세서](https://teamsparta.notion.site/API-d684258e92ef4c7797e406cf2f1e8f3c)

<br>

## 🌐 Architecture

![architecture PNG](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FdrtUIW%2FbtsJt7nMSuM%2FqwA8EJ2y42CxYTFEkR5s9K%2Fimg.png)

<br>

## [📋 ERD Diagram](https://www.erdcloud.com/d/ASKpbKPHEcdRhuWDb)

![banking_erd](https://github.com/user-attachments/assets/e2957ec5-f503-478c-822c-76c175fe51d4)

