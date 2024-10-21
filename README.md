# 가상화폐 모의 투자 서비스

## 프로젝트 개요
가상화폐 정보를 실시간으로 제공하고, 사용자들이 가상으로 암호화폐에 투자할 수 있는 모의 투자 서비스입니다.  
사용자들은 투자 정보를 공유하고, 다른 사용자들과 소통할 수 있는 커뮤니티 기능을 포함합니다.

## 기술 스택
- **Back-end**: Java, Spring Boot, REST API  
- **Database**: MySQL  
- **IDE**: Eclipse  

---

## 주요 기능 및 개발 우선순위

### 1. 가상화폐 정보 제공
- 실시간으로 가상화폐 정보(가격, 시가총액, 24시간 변동률 등)를 제공
- API 호출 방식 사용 (https://api.coinpaprika.com/)
- 초당 API 요청 수 제한에 따른 **시간 당 호출 횟수 제한** 고려
- 새로고침 시 위 정보를 보여줍니다 (초/밀리초 단위 실시간 변동 갱신은 하지 않음)
- 사용자가 요청시 최근 24시간 변동률 (24시간, 12시간, 6시간, 3시간, 1시간)을 제공

### 2. 회원가입 및 CRUD 기능
- 사용자 회원가입, 권한 부여, 로그인, 회원 정보 수정 및 삭제
- **JWT 인증**을 통한 사용자 인증 및 권한 부여 시스템 구현

### 3. 모의 투자 기능
- 초기 시드머니 100만 원(KRW) 제공
- 사용자는 가상화폐를 검색 후 매수/매도
- 매수/매도 시점의 가격을 기준으로 거래 진행
- 투자 포트폴리오 관리 및 수익률 계산
- 최대 투자 금액 제한: 10억 원(KRW)
- 빚 보유 상태 패널티: 자산 가치가 0원 미만일 경우 부과
- 빚은 초기화 이후에도 유지

### 4. 커뮤니티 기능
- 사용자가 자신의 모의 투자 정보를 **게시물로 공유** 가능
- 다른 사용자가 **댓글을 남겨 소통**할 수 있음

---

## 5. 추가 기능 (여유가 있다면 구현)
- 프론트엔드 작업
- 매수/매도 **옵션 설정** (예: 특정 가격 도달 시 매수/매도)
- 포트폴리오 평가 금액 **비율 유지 기능** (동결)
- **레버리지 및 공매도/공매수 기능** 추가
- 커뮤니티의 **대댓글 및 공감 기능**
- **그래프 및 시세표** 표시 (프론트엔드 작업)

---

## API 플로우 정리

### 1. 가상화폐 정보 제공 플로우
- 사용자 요청(가상화폐 조회/갱신) -> 백엔드가 외부 API 호출(파프리카 API) -> API 응답 처리 -> 사용자에게 실시간 데이터 반환
- 외부 API 호출로 **실시간 데이터** 제공.
- 초당 호출 제한에 도달하지 않도록 **시간당 호출 제한** 설정.

### 2. 사용자 투자 정보 플로우
- 사용자 요청 -> API 호출 -> DB에서 사용자 투자 정보 조회 -> 가상화폐 현재 가격과 투자 금액 계산 -> 사용자에게 응답
- DB에서 기존 투자 내역을 조회하고, 외부 API에서 **실시간 가격**을 가져와 수익률 및 평가 금액 계산 후 응답.

---

## 모의 투자 기능 세부 설계

- **검색 및 선택** 후 매수/매도 가능.
- 최대 투자 금액: **10억 원(KRW)** 제한.
- 초기 자산 **100만 원(KRW)**으로 재시작 가능 (시간당 초기화 제한).
- **공매도/공매수 패널티**:
  - 자산 가치가 0원 미만이 되면 **빚 보유 상태**로 패널티 부여.
  - 초기화 시에도 빚은 남아 있으며, 커뮤니티에 **빚 정보 표시**.

---

# ERD

### 1. 회원 테이블 (User Table)
| 컬럼명      | 설명       | 자료형        | 제약조건                 |
|-------------|------------|--------------|-------------------------|
| user_id     | 회원 ID     | BIGINT       | PK                      |
| username    | 사용자명     | VARCHAR(255) |                         |
| password    | 비밀번호     | VARCHAR(255) |                         |
| email       | 이메일       | VARCHAR(255) |                         |
| phone       | 연락처       | VARCHAR(11)  |                         |
| role        | 역할         | VARCHAR(20)  |                         |
| balance     | 잔액         | DECIMAL(15,2)| 기본값: 1,000,000 KRW  |
| created_at  | 가입일       | DATETIME     |                         |

---

### 2. 거래 테이블 (Transaction Table)
| 컬럼명          | 설명         | 자료형        | 제약조건        |
|-----------------|--------------|--------------|----------------|
| transaction_id  | 거래 ID      | BIGINT       | PK             |
| user_id         | 회원 ID      | BIGINT       | FK (User)      |
| crypto_symbol   | 가상화폐 심볼| VARCHAR(10)  |                |
| transaction_type| 거래 유형    | ENUM('BUY', 'SELL') |            |
| amount          | 거래 수량    | DECIMAL(18,8)|                |
| price           | 거래 가격    | DECIMAL(15,2)|                |
| transaction_time| 거래 일시    | DATETIME     |                |

---

### 3. 원장 테이블 (Ledger Table)
| 컬럼명         | 설명           | 자료형        | 제약조건        |
|----------------|----------------|--------------|----------------|
| ledger_id      | 원장 ID        | BIGINT       | PK             |
| user_id        | 회원 ID        | BIGINT       | FK (User)      |
| crypto_symbol  | 가상화폐 심볼  | VARCHAR(10)  |                |
| total_investment | 총 투자 금액 | DECIMAL(15,2)|                |
| current_amount| 현재 보유 수량 | DECIMAL(18,8)|                |
| profit_or_loss| 수익/손실 금액 | DECIMAL(15,2)|                |

---

### 4.포트폴리오 테이블 (Portfolio Table)

| 컬럼명           | 설명           | 자료형         | 제약조건        |
|------------------|----------------|---------------|----------------|
| portfolio_id     | 포트폴리오 ID  | BIGINT        | PK             |
| user_id          | 회원 ID        | BIGINT        | FK (User)      |
| total_investment | 총 투자 금액    | DECIMAL(15,2) |                |
| profit_or_loss   | 총 수익/손실    | DECIMAL(15,2) |                |
| created_at       | 생성일         | DATETIME      |                |
| updated_at       | 수정일         | DATETIME      |                |

---

### 5. 보유 자산 테이블 (Asset Table)
| 컬럼명           | 설명            | 자료형         | 제약조건        |
|------------------|----------------|---------------|----------------|
| asset_id         | 자산 ID         | BIGINT        | PK             |
| portfolio_id     | 포트폴리오 ID   | BIGINT        | FK (Portfolio) |
| crypto_symbol    | 가상화폐 심볼   | VARCHAR(10)   |                |
| amount           | 보유 수량       | DECIMAL(18,8) |                |
| average_price    | 평균 매수 가격  | DECIMAL(15,2) |                |
| current_value    | 현재 평가 금액  | DECIMAL(15,2) |                |
| created_at       | 생성일          | DATETIME      |                |
| updated_at       | 수정일          | DATETIME      |                |

---

### 6. 커뮤니티 게시물 테이블 (Community Post Table)
| 컬럼명     | 설명     | 자료형        | 제약조건        |
|------------|----------|--------------|----------------|
| post_id    | 게시물 ID| BIGINT       | PK             |
| user_id    | 회원 ID  | BIGINT       | FK (User)      |
| content    | 내용     | TEXT         |                |
| created_at | 작성일   | DATETIME     |                |

---

### 7. 댓글 테이블 (Comment Table)
| 컬럼명     | 설명      | 자료형        | 제약조건        |
|------------|-----------|--------------|----------------|
| comment_id | 댓글 ID   | BIGINT       | PK             |
| post_id    | 게시물 ID | BIGINT       | FK (Post)      |
| user_id    | 회원 ID   | BIGINT       | FK (User)      |
| comment    | 댓글 내용 | TEXT         |                |
| commented_at | 작성일  | DATETIME     |                |


![ERD](https://github.com/user-attachments/assets/159e06d6-7680-479f-a4d8-d8327aee73b3)

