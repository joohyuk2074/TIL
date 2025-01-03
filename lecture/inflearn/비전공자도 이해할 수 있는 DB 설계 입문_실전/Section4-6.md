# DB 설계 규칙 6가지

## 6. 숨어있는 중복을 찾아라

데이터베이스 설계 시 **숨어있는 중복**은 명시적으로 드러나지 않지만, 설계의 비효율로 인해 데이터 무결성과 관리에 문제를 야기할 수 있다. 특히, 관련된 데이터를 별도의 테이블로 관리할 경우 숨은 중복이 발생할 가능성이 높다.

---

### **예시: 조회수 관리에서의 숨은 중복**

**[잘못된 설계: 게시물 테이블에서 조회수를 관리]**

**posts (게시물)**

| id (PK) | 제목       | 내용      | 조회수 |
|---------|------------|-----------|-------|
| 1       | 게시물 1   | 내용 1    | 100   |
| 2       | 게시물 2   | 내용 2    | 50    |

- **문제점:** 
  - 조회수는 동적인 데이터로, 게시물 테이블의 다른 정적인 데이터(제목, 내용)와 성격이 다르다.
  - 조회수 업데이트가 자주 발생하면 게시물 테이블에 불필요한 수정 작업이 자주 일어나 성능에 영향을 미친다.

---

**[해결 방법1: 조회수를 별도의 테이블로 분리]**

**posts (게시물)**

| id (PK) | 제목       | 내용      |
|---------|------------|-----------|
| 1       | 게시물 1   | 내용 1    |
| 2       | 게시물 2   | 내용 2    |

**post_views (조회수)**

| id (PK) | 게시물 id (FK) | 조회수 |
|---------|----------------|-------|
| 1       | 1              | 100   |
| 2       | 2              | 50    |

- **장점:** 
  - 조회수 업데이트는 `post_views` 테이블만 수정하므로, `posts` 테이블의 성능에 영향을 주지 않음.
  - 조회수와 게시물 데이터를 독립적으로 관리할 수 있음.

<br>

**[해결 방법: 조회수를 동적으로 계산]**

- 조회수를 별도로 저장하지 않고, 조회 기록 테이블을 기반으로 조회수를 계산하도록 설계.

**posts (게시물)**

| id (PK) | 제목       | 내용      |
|---------|------------|-----------|
| 1       | 게시물 1   | 내용 1    |
| 2       | 게시물 2   | 내용 2    |

**post_views (조회 기록)**

| id (PK) | 게시물 id (FK) | 사용자 id | 조회 일시          |
|---------|----------------|-----------|--------------------|
| 1       | 1              | 101       | 2024-01-01 10:00  |
| 2       | 1              | 102       | 2024-01-01 10:05  |
| 3       | 2              | 103       | 2024-01-01 11:00  |

- **조회수 계산 쿼리 예시**
  ```sql
  SELECT 게시물.id, 제목, 내용, COUNT(post_views.id) AS 조회수
  FROM posts
  LEFT JOIN post_views ON posts.id = post_views.게시물_id
  GROUP BY posts.id, 제목, 내용;
