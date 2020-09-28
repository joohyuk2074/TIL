## 클론 네이버 예약 서비스

### 소개

- 기본적인 CRUD 게시판 입니다.
- SpringSecurity를 이용하여 권한에 따른 기능을 제한하였고 Remember-Me 기능을 구현아였습니다.
- 제목, 작성자별로 검색기능을 구현하였습니다.
<hr>

### 1. 게시판 페이지

<br>

<img width="250" alt="메인화면" src="https://user-images.githubusercontent.com/26062056/93191596-acc94400-f77f-11ea-8283-dae45f319ba7.png">

- 처음 접속하면 나타나는 메인화면으로 카테고리별로 상품 정보가 4개씩 보여진다.
- 더보기를 클릭하여 4개씩 추가로 상품정보를 AJAX요청으로 가져온다.
- 상품을 클릭하면 상품 상세정보 페이지로 이동한다.
<hr>

### 사용 기술

#### 프론트엔드

- HTML, CSS, JavaScript
- Thymeleaf
  
#### 백엔드

- SpringBoot
- SpringSecurity
- Spring Data JPA
- QueryDsl

#### DB

- MySql
- H2

#### 빌드 툴

- gradle
