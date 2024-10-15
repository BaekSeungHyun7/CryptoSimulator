가상화폐 모의 투자 서비스

<프로젝트 주제>
가상화폐 정보를 실시간으로 제공하고, 사용자들이 가상으로 암호화폐에 투자할 수 있는 모의 투자 서비스로 사용자들은 투자 정보를 공유하고, 다른 사용자들과 소통할 수 있는 커뮤니티 기능이 포합됩니다.

<주요 기능>
1. 가상화폐 정보제공
- 실시간으로 가상화폐 정보(가격, 시가총액, 24시간 변동률 등)를 제공
- 데이터를 업데이트하여 사용자에게 최신 정보를 제공합니다.

2. 회원가입 및 CRUD 기능
- 사용자 회원가입, 로그인, 회원 정보 수정 및 삭제 
- JWT를 사용한 인증 및 권한 부여 시스템을 구현

3. 모의 투자 기능
- 사용자가 선택한 가상화폐에 대해 모의 투자를 할 수 있음
- 사용자는 가상 포트폴리오를 관리하고, 모의 투자 기록을 확인 가능

4. 커뮤니티 기능
- 사용자가 자신의 모의 투자 정보를 게시물로 공유 가능
- 다른 사용자들이 게시물에 댓글을 남겨 소통할 수 있음

기술 스택
Back-end: Java, Spring Boot, REST API
Database: MySQL
IDE: Eclipse

데이터베이스 설계

회원 테이블
member_id (BIGINT, PK): 회원 고유 ID
username (VARCHAR): 회원 이름
password (VARCHAR): 비밀번호
email (VARCHAR): 이메일
role (VARCHAR): 사용자 권한 (USER, ADMIN 등)

가상화폐 정보 테이블
crypto_id (BIGINT, PK): 암호화폐 고유 ID
name (VARCHAR): 암호화폐 이름
symbol (VARCHAR): 암호화폐 기호
price (DOUBLE): 가격
market_cap (DOUBLE): 시가총액
volume (DOUBLE): 거래량
change_24h (DOUBLE): 24시간 변동률

투자 기록 테이블
investment_id (BIGINT, PK): 투자 고유 ID
member_id (BIGINT, FK): 회원 ID
crypto_id (BIGINT, FK): 투자한 암호화폐 ID
amount (DOUBLE): 투자한 암호화폐 수량
price_at_investment_usd (DOUBLE): 투자 당시 달러 가격
price_at_investment_krw (DOUBLE): 투자 당시 원화 가격
created_at (DATETIME): 투자 시각

게시물 테이블
post_id (BIGINT, PK): 게시물 고유 ID
member_id (BIGINT, FK): 작성자 ID
content (TEXT): 게시물 내용
created_at (DATETIME): 작성일

댓글 테이블
comment_id (BIGINT, PK): 댓글 고유 ID
post_id (BIGINT, FK): 게시물 ID
member_id (BIGINT, FK): 작성자 ID
content (TEXT): 댓글 내용
created_at (DATETIME): 작성일

API 엔드포인트
회원 관리
POST /auth/signup: 회원가입
POST /auth/signin: 로그인
GET /auth/profile: 회원 정보 조회
PUT /auth/profile: 회원 정보 수정
DELETE /auth/profile: 회원 탈퇴

가상화폐 정보
GET /cryptos: 가상화폐 목록 조회
GET /cryptos/{id}: 특정 가상화폐 정보 조회

나의 모의 투자
POST /investments: 모의 투자 실행
GET /investments: 나의 투자 기록 조회

커뮤니티
POST /posts: 게시물 작성
GET /posts: 게시물 목록 조회
GET /posts/{id}: 특정 게시물 조회
PUT /posts/{id}: 게시물 수정
DELETE /posts/{id}: 게시물 삭제
POST /posts/{id}/comments: 댓글 작성
GET /posts/{id}/comments: 댓글 목록 조회
