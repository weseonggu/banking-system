<br>

# MSA 기반의 온라인 은행 뱅킹 시스템 플랫폼

<img src="/gitimg/브로셔이미지.png" alt="은행 이미지" width="500" height="300"/>



## 📝 Technologies & Tools (BE) 📝

## DB
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)

## Messaging
![Kafka](https://img.shields.io/badge/Apache%20Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)

## Tech
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)
![Querydsl](https://img.shields.io/badge/QueryDSL-FF6F00?style=for-the-badge)
![Eureka](https://img.shields.io/badge/Eureka-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![OpenFeign](https://img.shields.io/badge/OpenFeign-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Redisson](https://img.shields.io/badge/Redisson-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Spring Batch](https://img.shields.io/badge/Spring%20Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Data](https://img.shields.io/badge/Spring%20Data-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Event Listener](https://img.shields.io/badge/Event%20Listener-FFA500?style=for-the-badge)

## Monitoring
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white)
![Zipkin](https://img.shields.io/badge/Zipkin-000000?style=for-the-badge&logo=zipkin&logoColor=white)

## Test
![JMeter](https://img.shields.io/badge/JMeter-D22128?style=for-the-badge&logo=apachejmeter&logoColor=white)

## CI/CD
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)
![Amazon ECR](https://img.shields.io/badge/Amazon%20ECR-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)
![Amazon EC2](https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)


## 👨‍👩‍👧‍👦 Our Team

|                    김원기                     |                      김혜린                       |                 위성구                 |                 이범수                 |
|:------------------------------------------:|:----------------------------------------------:|:-----------------------------------:|:-----------------------------------:|
| [@TrendFollow](https://github.com/TrendFollow) | [@uzuberceuse](https://github.com/uzuberceuse) | [@weseonggu](https://github.com/weseonggu) | [@beomsu1](https://github.com/beomsu1) |
|                     BE                     |                       BE                       |                 BE                  |                 BE                  |
<br>

### 개발 기간
`2024.09.24 ~ 2024.10.25`

## 🌈 서비스/프로젝트 목표
<aside>
<b>확장성과 가용성</b>

> * MSA  아키텍처를 적용하여 서비스 기능을 독립적으로 운영하고 확장 가능하게 설계했습니다.
> * Kafka 를 활용한 비동기 메시징 시스템으로 대규모 트래픽을 원활하게 처리했습니다.
</aside>
<aside>
<b>동시성 이슈 관리</b>

> * Redisson 라이브러리를 사용해 동시성 제어 및 분산 락을 구현하여 데이터 정합성을 유지하면서 빠른 응답 시간을 제공합니다.
</aside>

<aside>


<b>보안과 데이터 무결성 강화</b>

> * Spring Security를 통한 권한 관리와 인증/인가 구현했습니다.
> * JWT 기반 인증으로 무상태 세션 유지 및 안전한 사용자 인증 체계 구축했습니다.

</aside>

<aside>

<b>Redis 캐시를 통한 성능 최적화</b>

> * Redis 캐시를 활용해 자주 조회되는 데이터를 캐싱함으로써 데이터베이스 부하를 감소시키고 응답 속도 향상시켰습니다.
> * 사용자 인증 및 세션 데이터 캐싱으로 시스템 성능과 안정성 강화했습니다.

</aside>

<aside>

<b>멀티모듈 아키텍처 사용</b>

> * 공통 코드와 기능을 모듈화**하여 재사용성을 높이고, 유지/보수를 용이하게 개선했습니다.
> * 각 모듈이 독립적으로 개발 및 테스트가 이루어졌으며 통합된 애플리케이션에서 일관되게 동작하도록 구성했습니다.

</aside>

<aside>

<b>Docker 를 활용한 배포 및 운영</b>

> * Docker**를 사용해 각 서비스를 컨테이너화하여 일관된 개발 환경과 운영 환경을 보장합니다.
> * 배포 자동화와 확장성을 지원하여 서비스 안정성을 유지하고, 운영 효율성 극대화시켰습니다.

</aside>

## ⛱️ 인프라 설계도
<img src="/gitimg/Infra.png" alt="은행 이미지" />

## ⛱ 기술적 의사결정
[쓰기성능 향상] CQRS 패턴 [WIKI보기](https://github.com/Last-Project-20/banking-system/wiki/%5B%EC%93%B0%EA%B8%B0%EC%84%B1%EB%8A%A5-%ED%96%A5%EC%83%81%5D-CQRS-%ED%8C%A8%ED%84%B4)

[데이터 동시성] 동시성 문제 해결을 위한 분산락 [WIKI보기](https://github.com/Last-Project-20/banking-system/wiki/%5B%EB%8D%B0%EC%9D%B4%ED%84%B0-%EB%8F%99%EC%8B%9C%EC%84%B1%5D-%EB%8F%99%EC%8B%9C%EC%84%B1-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0%EC%9D%84-%EC%9C%84%ED%95%9C-%EB%B6%84%EC%82%B0%EB%9D%BD)

[공통 모듈] 멀티 모듈 구성 [WIKI보기](https://github.com/Last-Project-20/banking-system/wiki/%5B%EA%B3%B5%ED%86%B5-%EB%AA%A8%EB%93%88%5D-%EB%A9%80%ED%8B%B0-%EB%AA%A8%EB%93%88-%EA%B5%AC%EC%84%B1)

[인증/인가] 스프링 시큐리티 vs Gateway [WIKI보기](https://github.com/Last-Project-20/banking-system/wiki/%5B%EC%9D%B8%EC%A6%9D-%EC%9D%B8%EA%B0%80%5D-%EC%8A%A4%ED%94%84%EB%A7%81-%EC%8B%9C%ED%81%90%EB%A6%AC%ED%8B%B0-vs-Gateway)

[실시간 데이터 처리] Kafka vs DB 변경 감지 [WIKI보기](https://github.com/Last-Project-20/banking-system/wiki/%5B%EC%8B%A4%EC%8B%9C%EA%B0%84-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%B2%98%EB%A6%AC%5D-Kafka-vs-DB-%EB%B3%80%EA%B2%BD-%EA%B0%90%EC%A7%80)

[대용량 데이터 처리] Spring Batch [WIKI보기](https://github.com/Last-Project-20/banking-system/wiki/%5B%EB%8C%80%EC%9A%A9%EB%9F%89-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%B2%98%EB%A6%AC%5D-Spring-Batch)

## 🔧 주요 기능
<h3>🍈 상품 서비스</h3>
<details>

  <summary>상품 서비스</summary>

**상품 서비스 기능**

1. **상품 등록:**
    - 게시판 형식으로 상품에 대한 설명을 작성합니다.
    - 세부적인 내용을 확인할 수 있는 pdf 자료를 업로드합니다.

2. **상품 가입:**
       - 소비자에게 적합한 상품을 가입할 수 있습니다.
       - 가입 시 필요한 서류를 선택하고 제출할 수 있습니다.
       - 일부 상품의 경우 직업의 요인이 있어야 사용이 가능합니다.

3. **대출 기능:**
       - 사용자의 현재 대출 상태를 관리하고 대출 상환 계획을 제시합니다.
       - 이름을 통해 사용자는 현재 대출 이자율, 상환 기간 등을 쉽게 확인하고, 상환 시점을 확인할 수 있습니다.

</details>

<h3>🍊계좌 서비스</h3>
<details>
  <summary>계좌 서비스</summary>

**계좌 서비스 기능**

1. **계좌:**
    - 상품 가입 시 해당 서비스를 혼합하면 고객 명의로 계좌가 등록됩니다.
    - 금융 거래 내역 및 세제와 관련된 사항이 통합됩니다.
    - 사용자는 모바일 앱을 통해 자신의 계좌 상태 및 상환 상태를 변동합니다.
    - 계좌에 대한 상세한 조회가 가능합니다.

2. **입출금 및 이체:**
    - 입/출금, 이체 거래가 가능합니다.
    - 거래 내역에 대한 설명을 수령할 수 있습니다.
    - 거래에 대한 상세한 조회가 가능합니다.

3. **자동 이체:**
    - 동일 은행 계좌에 대해 매달 특정 일자에 대해 자동 이체가 가능합니다.
    - 이체 금액과 이체 일자에 대한 변경이 가능합니다.
    - 자동 이체를 취소할 수 있습니다.
    - 자동 이체 내역에 대해 조회가 가능합니다.

</details>

<h3>🍋 개인 예산 서비스</h3>
<details>
  <summary>개인 예산 서비스</summary>

**개인 예산 관리 서비스 기능**

1. **개인 예산 관리:**
    - 사용자의 거래 시 개인 예산 내역 생성 → 카테고리 설정 가능
    - 설정한 기간 내 거래 항목에 맞춘 금액을 소비한 카테고리, 총 소비 금액 조회 가능

2. **예산 알림:**
    - 기간을 정해 사용 예산을 설정
    - 기간 내 또는 사용금액이 설정한 예산을 넘기면 알림 발송

</details>

## 🧨 트러블 슈팅
성능테스트 상품 조회 트러블 슈팅 [WIKI보기](https://github.com/Last-Project-20/banking-system/wiki/%5BTrouble-Shooting%5D-%EC%84%B1%EB%8A%A5%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%83%81%ED%92%88-%EC%A1%B0%ED%9A%8C-%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85)

성능테스트 회원 수정 트러블 슈팅 [WIKE보기](https://github.com/Last-Project-20/banking-system/wiki/%5BTrouble-Shooting%5D-%EC%84%B1%EB%8A%A5%ED%85%8C%EC%8A%A4%ED%8A%B8-%ED%9A%8C%EC%9B%90-%EC%88%98%EC%A0%95-%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85)

참조키 문제 [WIKI보기](https://github.com/Last-Project-20/banking-system/wiki/%5BTrouble-Shooting%5D-%EC%B0%B8%EC%A1%B0%ED%82%A4-%EB%AC%B8%EC%A0%9C)

트랜잭션 롤백 : 서비스 분리와 트랜잭션 전파의 중요성 [WIKI보기](https://github.com/Last-Project-20/banking-system/wiki/%5BTrouble-Shooting%5D-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EB%A1%A4%EB%B0%B1-:-%EC%84%9C%EB%B9%84%EC%8A%A4-%EB%B6%84%EB%A6%AC%EC%99%80-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EC%A0%84%ED%8C%8C%EC%9D%98-%EC%A4%91%EC%9A%94%EC%84%B1)

## 🍇 CONTRIBUTORS


| 팀원명     | 포지션 | 담당(개인별 기여점)                                                                                                                                                                                                                                                                                                    | GitHub 링크 |
|---------|--------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------|
| **김원기** | 리더   | **GitHub 관리** / 팀원 간 일정 조율 및 튜터님과 일정 협의 <br> - **인증/인가 및 Gateway 필터 구현**<br>    - 시큐리티를 통한 인증/인가 구현 / Gateway를 통한 JWT 검증 및 라우팅<br>  **Auth Service**<br>    - 회원 슬랙 ID 검증, 회원가입 알림, 로그인, 로그아웃 구현<br> - **User Service**<br>    - User 검색 및 수정 구현<br>  **Performance Service**<br>    - 원별, 연도별 대출 가입건수 및 대출 상환 금액을 스케줄로 통해 자동 기록 | [GitHub TrendFollow - Overview](https://github.com/TrendFollow) |
| **김혜린** | 브리더 |  팀원 간 소통이 원활하도록 분위기 조성 및 참여 유도<br>     팀 회의 시 대화 주도 및 팀원 참여 유도<br>     팀 주요 공지 사항 안내<br>  **기록 및 발표 자료 담당**<br>     팀 회의/튜터님 피드백 기록 작성<br>     제출 자료 검토 및 정리<br>  **Account Service**<br>    - 계좌, 금융 거래, 자동 이체 관련 기능 구현                                                                                       | [GitHub uzuberceuse - Overview](https://github.com/uzuberceuse) |
| **위성구** | 팀원   |  기획 단계에서 선택할 기술을 파악하고 대안을 제공<br>     메인 서비스의 금융 거래에 대해서 다양한 구현 방식을 제안<br>     프로젝트 생성 및 모듈 구조 설정<br>  **Product Service**<br>    - 상품 생성 조회 구현<br>    - 고객의 상품 가입, 조회, 실행, 해지 구현<br>  **배포 관련**<br>    - 배포를 위한 AWS 운영<br>    - Docker-compose 작성<br>  **모니터링**<br>    - Prometheus, Grafana를 사용한 서버의 자원 사용량 모니터링 | [GitHub wseongsung - Overview](https://github.com/wseongsung) |
| **이범수** | 팀원   |  **Personal Service**<br>    - 개인 내역 생성, 카테고리 설정<br>    - 예산 설정 생성, 수정<br>    - 기간 내 사용 비율이 설정한 예산을 넘으면 알림 발송<br>  **CI/CD**<br>    - GitHub Actions를 통한 CI/CD 파이프라인 구축<br>    - ECR에 도커 이미지를 저장하고, EC2에서 컨테이너로 실행                                                                                             | [GitHub beomsu1 - Overview](https://github.com/beomsu1) |

