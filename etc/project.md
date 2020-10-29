# 프로젝트하면서 문제점 극복 과정

## 네이버 예약 시스템 프로젝트
1. 데이터를 조회하는 과정에서 관계형의 테이블에 맞게 만든 엔티티에 데이터를 api스펙에 맞게 바꾸는 과정에서 서비스계층에서 기능을 실행하는 로직보다 데이터를 바인딩하는코드가 많아져 코드가 길어지고 가독성이 떨어지는 문제점을 발견했고 반복적인 CRUD 쿼리 작성과 

## 커뮤니티 사이트 게시판 프로젝트

## 유튜브 클론 프로젝트
동영상 녹화 후 영상 시청시 전체 시간이 NaN으로 표시되어 있어 videoPlayer.js 디버깅을 해보니 video의 durationdl infinity로 되어 있었음

## 공통
1. 통합테스트 작성 시 인증 후 인증객체를 이용해서 테스트를 하는 경우
```
@WithMockUser를 주면 Authentication안에 UsernamePasswordAuthenticationToken타입으로 username을 "user", password를 "password"를 넣어주는데 쓸 수 없다. 진짜 DB에 저장된 데이터가 SecurityContext에 들어 있어야 한다.
@WithUserDetails("joohyuk")를 쓰고 @BeforeEach에 인증로직을 추가하여 테스트를 돌렸느데 @BeforeEach가 실행하기전에 @WithMockUser가 먼저 실행하여 인증객체를 가져오지 못하는 상황이 생겼습니다.
그래서 @WithMockUser에 setupBefore 옵션으로 @BeforeEach를 실행 후 실행하도록 했지만 여전히 똑같은 에러가 발생하였고 찾아보니 버그라고 합니다.
그래서 @WithSecurityContext를 이용하여 직접 SecurityContext를 만들어줄 팩토리를 만들어 테스트하는 방법을 사용하였습니다.
```

2. 게시글, 댓글 페이징처리
3. 
```java
// 현재 글이 포함된 부분의 첫 Page Number 구하기
starNumber = Math.floor(현재페이지 / 한번에 보여지는 페이지 수) * 한번에 보여지는 페이지 수 + 1 ;

// 현재 글이 포함된 부분의 마지막 Page Number 구하기
endNumber = if 전체페이지 수 > startNumber + (한번에 보여지는 페이지수 -1)
            then startNumber + (한번에 보여지는 페이지수 - 1)
            else 전체 페이지 수

```